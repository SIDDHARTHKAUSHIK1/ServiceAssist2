package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.ServoraDatabase
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.CustomerReview
import com.example.data.model.Offer
import com.example.data.model.Professional
import com.example.data.model.SavedAddress
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.ServicePackage
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.data.remote.supabase.SupabaseSyncManager
import com.example.data.remote.supabase.SupabaseSyncState
import com.example.data.repository.ServoraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class BookingDraft(
    val service: ServiceItem? = null,
    val selectedPackage: ServicePackage? = null,
    val selectedDate: String = "Today",
    val selectedTimeSlot: String = "02:00 PM",
    val selectedAddress: SavedAddress? = null,
    val appliedPromo: Offer? = null,
    val paymentMethod: String = "Cash after service", // "UPI (Google Pay / PhonePe)", "Card", "Cash after service"
    val specialNotes: String = ""
)

class ServoraViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ServoraDatabase.getDatabase(application, viewModelScope)

    private val syncManager = SupabaseSyncManager(
        bookingDao = database.bookingDao(),
        addressDao = database.addressDao(),
        reviewDao = database.reviewDao(),
        userDao = database.userDao(),
        scope = viewModelScope
    )

    val repository = ServoraRepository(
        bookingDao = database.bookingDao(),
        addressDao = database.addressDao(),
        reviewDao = database.reviewDao(),
        userDao = database.userDao(),
        syncManager = syncManager
    )

    val supabaseSyncState: StateFlow<SupabaseSyncState> = syncManager.syncState

    fun triggerSupabaseSync() {
        syncManager.triggerSync()
    }

    init {
        // Ensure default data is present if fresh install
        viewModelScope.launch {
            ServoraDatabase.populateInitialData(database)
        }
    }

    // Observables from Room
    val currentUser: StateFlow<UserProfile> = repository.currentUser
        .map { it ?: UserProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    val allBookings: StateFlow<List<Booking>> = repository.allBookings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeBooking: StateFlow<Booking?> = repository.activeBooking
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val savedAddresses: StateFlow<List<SavedAddress>> = repository.savedAddresses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allReviews: StateFlow<List<CustomerReview>> = repository.allReviews
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Catalog state
    val categories: List<ServiceCategory> = repository.categories
    val services: List<ServiceItem> = repository.services
    val professionals: List<Professional> = repository.professionals
    val offers: List<Offer> = repository.offers
    val supportedCities: List<String> = repository.supportedCities
    val agraLocalities: List<String> = repository.agraLocalities

    // Search & Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId = _selectedCategoryId.asStateFlow()

    val filteredServices = combine(
        _searchQuery,
        _selectedCategoryId
    ) { query, categoryId ->
        var list = services
        if (!categoryId.isNullOrBlank()) {
            list = list.filter { it.categoryId == categoryId }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.subtitle.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.whatIsIncluded.any { inc -> inc.lowercase().contains(q) }
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), services)

    // Booking Flow State
    private val _bookingDraft = MutableStateFlow(BookingDraft())
    val bookingDraft = _bookingDraft.asStateFlow()

    private val _confirmedBooking = MutableStateFlow<Booking?>(null)
    val confirmedBooking = _confirmedBooking.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

    fun startBooking(service: ServiceItem, defaultPackage: ServicePackage? = null) {
        val pkg = defaultPackage ?: service.packages.firstOrNull()
        val defaultAddr = savedAddresses.value.firstOrNull { it.isDefault } ?: savedAddresses.value.firstOrNull()
        _bookingDraft.value = BookingDraft(
            service = service,
            selectedPackage = pkg,
            selectedDate = "Today",
            selectedTimeSlot = "02:00 PM",
            selectedAddress = defaultAddr,
            appliedPromo = null,
            paymentMethod = "Cash after service"
        )
    }

    fun updateBookingDraft(
        pkg: ServicePackage? = _bookingDraft.value.selectedPackage,
        date: String = _bookingDraft.value.selectedDate,
        timeSlot: String = _bookingDraft.value.selectedTimeSlot,
        address: SavedAddress? = _bookingDraft.value.selectedAddress,
        promo: Offer? = _bookingDraft.value.appliedPromo,
        paymentMethod: String = _bookingDraft.value.paymentMethod,
        notes: String = _bookingDraft.value.specialNotes
    ) {
        _bookingDraft.value = _bookingDraft.value.copy(
            selectedPackage = pkg,
            selectedDate = date,
            selectedTimeSlot = timeSlot,
            selectedAddress = address,
            appliedPromo = promo,
            paymentMethod = paymentMethod,
            specialNotes = notes
        )
    }

    fun applyPromoCode(code: String): Boolean {
        val found = offers.find { it.code.equals(code.trim(), ignoreCase = true) }
        if (found != null) {
            _bookingDraft.value = _bookingDraft.value.copy(appliedPromo = found)
            return true
        }
        return false
    }

    fun removePromoCode() {
        _bookingDraft.value = _bookingDraft.value.copy(appliedPromo = null)
    }

    fun calculateTotalAmount(): Triple<Int, Int, Int> {
        val draft = _bookingDraft.value
        val basePrice = draft.selectedPackage?.price ?: draft.service?.startingPrice ?: 499
        var discount = 0
        draft.appliedPromo?.let { offer ->
            if (offer.percentageDiscount > 0) {
                discount = (basePrice * offer.percentageDiscount) / 100
            } else if (offer.flatDiscount > 0) {
                discount = offer.flatDiscount
            }
        }
        val finalPrice = (basePrice - discount).coerceAtLeast(49)
        return Triple(basePrice, discount, finalPrice)
    }

    fun confirmBooking(onSuccess: (Long) -> Unit) {
        val draft = _bookingDraft.value
        val service = draft.service ?: return
        val pkg = draft.selectedPackage
        val (_, discount, total) = calculateTotalAmount()
        val address = draft.selectedAddress
        val addressText = if (address != null) {
            "${address.fullAddress}, ${address.locality}, ${address.city}"
        } else {
            "Taj Nagri Phase 2, Agra"
        }

        val randomCode = "SRV-" + Random.nextInt(10000, 99999)
        val randomOtp = Random.nextInt(1000, 9999).toString()

        val newBooking = Booking(
            bookingCode = randomCode,
            serviceId = service.id,
            serviceName = service.name,
            packageName = pkg?.name ?: "Standard Service",
            scheduledDate = draft.selectedDate,
            scheduledTime = draft.selectedTimeSlot,
            addressText = addressText,
            locality = address?.locality ?: "Taj Nagri",
            city = address?.city ?: "Agra",
            totalAmount = total,
            discountAmount = discount,
            promoCode = draft.appliedPromo?.code ?: "",
            paymentMethod = draft.paymentMethod,
            isPaid = draft.paymentMethod.contains("UPI") || draft.paymentMethod.contains("Card"),
            status = BookingStatus.ASSIGNED,
            professionalId = "pro_rajesh_1",
            startOtp = randomOtp,
            specialNotes = draft.specialNotes
        )

        viewModelScope.launch {
            val id = repository.createBooking(newBooking)
            val created = newBooking.copy(id = id)
            _confirmedBooking.value = created
            onSuccess(id)
        }
    }

    fun advanceBookingStatus(bookingId: Long, currentStatus: BookingStatus) {
        val next = when (currentStatus) {
            BookingStatus.PENDING -> BookingStatus.CONFIRMED
            BookingStatus.CONFIRMED -> BookingStatus.ASSIGNED
            BookingStatus.ASSIGNED -> BookingStatus.ON_THE_WAY
            BookingStatus.ON_THE_WAY -> BookingStatus.ARRIVED
            BookingStatus.ARRIVED -> BookingStatus.STARTED
            BookingStatus.STARTED -> BookingStatus.COMPLETED
            BookingStatus.COMPLETED -> BookingStatus.COMPLETED
            BookingStatus.CANCELLED -> BookingStatus.CANCELLED
        }
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, next)
        }
    }

    fun cancelBooking(bookingId: Long) {
        viewModelScope.launch {
            repository.cancelBooking(bookingId)
        }
    }

    fun submitReview(serviceId: String, serviceName: String, proName: String, rating: Float, comment: String, tags: String) {
        viewModelScope.launch {
            repository.addReview(
                CustomerReview(
                    serviceId = serviceId,
                    serviceName = serviceName,
                    professionalName = proName,
                    customerName = currentUser.value.name,
                    rating = rating,
                    comment = comment,
                    tags = tags,
                    dateText = "Just now"
                )
            )
        }
    }

    fun addAddress(title: String, addressText: String, locality: String, landmark: String) {
        viewModelScope.launch {
            repository.addAddress(
                SavedAddress(
                    title = title,
                    fullAddress = addressText,
                    locality = locality,
                    city = currentUser.value.city,
                    landmark = landmark,
                    isDefault = savedAddresses.value.isEmpty()
                )
            )
        }
    }

    fun deleteAddress(id: Long) {
        viewModelScope.launch {
            repository.deleteAddress(id)
        }
    }

    fun switchRole(role: UserRole) {
        viewModelScope.launch {
            repository.updateUserRole(role)
        }
    }

    fun setLocation(city: String, locality: String) {
        viewModelScope.launch {
            repository.updateLocation(city, locality)
        }
    }

    fun seedDemoDataToSupabase() {
        viewModelScope.launch {
            repository.seedDemoDataToSupabase()
        }
    }
}
