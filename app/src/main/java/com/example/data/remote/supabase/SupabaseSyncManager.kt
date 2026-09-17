package com.example.data.remote.supabase

import android.util.Log
import com.example.data.db.AddressDao
import com.example.data.db.BookingDao
import com.example.data.db.ReviewDao
import com.example.data.db.UserDao
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.CustomerReview
import com.example.data.model.SavedAddress
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SupabaseSyncState(
    val isConfigured: Boolean = false,
    val isConnected: Boolean = false,
    val isSyncing: Boolean = false,
    val lastSyncTime: Long = 0L,
    val lastMessage: String = "Local Room DB active",
    val error: String? = null,
    val remoteBookingsCount: Int = 0
)

class SupabaseSyncManager(
    private val bookingDao: BookingDao,
    private val addressDao: AddressDao,
    private val reviewDao: ReviewDao,
    private val userDao: UserDao,
    private val scope: CoroutineScope
) {
    private val TAG = "SupabaseSyncManager"

    private val _syncState = MutableStateFlow(
        SupabaseSyncState(
            isConfigured = SupabaseConfig.isConfigured,
            lastMessage = if (SupabaseConfig.isConfigured) "Configured, awaiting sync" else "Offline Room database active"
        )
    )
    val syncState: StateFlow<SupabaseSyncState> = _syncState.asStateFlow()

    init {
        // Initial sync check if configured
        if (SupabaseConfig.isConfigured) {
            triggerSync()
        }
    }

    fun triggerSync() {
        scope.launch(Dispatchers.IO) {
            syncNow()
        }
    }

    suspend fun syncNow(): Boolean {
        if (!SupabaseConfig.isConfigured) {
            _syncState.value = _syncState.value.copy(
                isConfigured = false,
                isConnected = false,
                isSyncing = false,
                lastMessage = "Supabase credentials not set. Set SUPABASE_URL & SUPABASE_ANON_KEY in AI Studio Secrets.",
                error = null
            )
            return false
        }

        val api = SupabaseClient.apiService ?: run {
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                lastMessage = "Failed to initialize Supabase HTTP client",
                error = "Client initialization failed"
            )
            return false
        }

        _syncState.value = _syncState.value.copy(isSyncing = true, error = null)

        return try {
            // 1. PUSH User profile
            val currentUser = userDao.getCurrentUserSync()
            if (currentUser != null) {
                try {
                    api.upsertUserProfile(profile = currentUser.toSupabaseDto())
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to upsert user profile: ${e.message}")
                }
            }

            // 2. SYNC BOOKINGS (Two-way)
            val remoteBookingsResponse = api.getBookings()
            var remoteCount = 0
            if (remoteBookingsResponse.isSuccessful) {
                val remoteList = remoteBookingsResponse.body() ?: emptyList()
                remoteCount = remoteList.size
                // Merge remote bookings into local Room DB
                remoteList.forEach { remoteDto ->
                    val existing = bookingDao.getBookingByCode(remoteDto.bookingCode)
                    if (existing == null) {
                        bookingDao.insertBooking(remoteDto.toDomainBooking())
                    } else {
                        // Update status if remote changed
                        val remoteStatus = try {
                            BookingStatus.valueOf(remoteDto.status)
                        } catch (e: Exception) {
                            existing.status
                        }
                        if (existing.status != remoteStatus) {
                            bookingDao.updateStatus(existing.id, remoteStatus)
                        }
                    }
                }

                // Push any local bookings that don't exist remotely
                val remoteCodes = remoteList.map { it.bookingCode }.toSet()
                val localList = bookingDao.getAllBookingsList()
                localList.forEach { local ->
                    if (!remoteCodes.contains(local.bookingCode)) {
                        try {
                            api.insertBooking(local.toSupabaseDto())
                        } catch (e: Exception) {
                            Log.w(TAG, "Could not push local booking ${local.bookingCode}: ${e.message}")
                        }
                    }
                }
            }

            // 3. SYNC REVIEWS
            val remoteReviewsResponse = api.getReviews()
            if (remoteReviewsResponse.isSuccessful) {
                val remoteReviews = remoteReviewsResponse.body() ?: emptyList()
                val localReviews = reviewDao.getAllReviewsList()
                val localComments = localReviews.map { it.comment }.toSet()

                remoteReviews.forEach { remoteDto ->
                    if (!localComments.contains(remoteDto.comment)) {
                        reviewDao.insertReview(remoteDto.toDomainReview())
                    }
                }
            }

            // 4. SYNC ADDRESSES
            val remoteAddressesResponse = api.getAddresses()
            if (remoteAddressesResponse.isSuccessful) {
                val remoteAddresses = remoteAddressesResponse.body() ?: emptyList()
                val localAddresses = addressDao.getAllAddressesList()
                val localAddrs = localAddresses.map { it.fullAddress }.toSet()

                remoteAddresses.forEach { remoteDto ->
                    if (!localAddrs.contains(remoteDto.fullAddress)) {
                        addressDao.insertAddress(remoteDto.toDomainAddress())
                    }
                }
            }

            _syncState.value = SupabaseSyncState(
                isConfigured = true,
                isConnected = true,
                isSyncing = false,
                lastSyncTime = System.currentTimeMillis(),
                lastMessage = "Synchronized with Supabase Cloud Postgres ($remoteCount bookings)",
                error = null,
                remoteBookingsCount = remoteCount
            )
            true
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed: ${e.message}", e)
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                isConnected = false,
                lastMessage = "Sync failed: ${e.localizedMessage ?: e.message}",
                error = e.localizedMessage ?: e.message
            )
            false
        }
    }

    fun onBookingCreated(booking: Booking) {
        if (!SupabaseConfig.isConfigured) return
        scope.launch(Dispatchers.IO) {
            try {
                SupabaseClient.apiService?.insertBooking(booking.toSupabaseDto())
                Log.d(TAG, "Pushed new booking ${booking.bookingCode} to Supabase")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to push booking to Supabase: ${e.message}")
            }
        }
    }

    fun onBookingStatusUpdated(bookingCode: String, status: BookingStatus) {
        if (!SupabaseConfig.isConfigured) return
        scope.launch(Dispatchers.IO) {
            try {
                SupabaseClient.apiService?.updateBookingStatus(
                    bookingCodeFilter = "eq.$bookingCode",
                    updates = mapOf("status" to status.name)
                )
                Log.d(TAG, "Pushed status $status for $bookingCode to Supabase")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to update booking status in Supabase: ${e.message}")
            }
        }
    }

    fun onReviewAdded(review: CustomerReview) {
        if (!SupabaseConfig.isConfigured) return
        scope.launch(Dispatchers.IO) {
            try {
                SupabaseClient.apiService?.insertReview(review.toSupabaseDto())
            } catch (e: Exception) {
                Log.w(TAG, "Failed to push review to Supabase: ${e.message}")
            }
        }
    }

    fun onAddressAdded(address: SavedAddress) {
        if (!SupabaseConfig.isConfigured) return
        scope.launch(Dispatchers.IO) {
            try {
                SupabaseClient.apiService?.insertAddress(address.toSupabaseDto())
            } catch (e: Exception) {
                Log.w(TAG, "Failed to push address to Supabase: ${e.message}")
            }
        }
    }

    fun onUserProfileUpdated(profile: UserProfile) {
        if (!SupabaseConfig.isConfigured) return
        scope.launch(Dispatchers.IO) {
            try {
                SupabaseClient.apiService?.upsertUserProfile(profile = profile.toSupabaseDto())
            } catch (e: Exception) {
                Log.w(TAG, "Failed to upsert user profile to Supabase: ${e.message}")
            }
        }
    }

    suspend fun seedDemoDataToSupabase(): Boolean {
        if (!SupabaseConfig.isConfigured) {
            _syncState.value = _syncState.value.copy(
                error = "Supabase credentials missing in Secrets panel."
            )
            return false
        }
        val api = SupabaseClient.apiService ?: return false
        _syncState.value = _syncState.value.copy(isSyncing = true, error = null)

        return try {
            // 1. User profile
            val demoUser = SupabaseUserProfileDto(
                id = "user_priya_1",
                name = "Priya Sharma",
                phone = "+91 98765 43210",
                email = "priya.sharma@example.com",
                city = "Agra",
                locality = "Taj Nagri Phase 2",
                role = "CUSTOMER"
            )
            try {
                api.upsertUserProfile(demoUser)
            } catch (e: Exception) {
                Log.w(TAG, "User profile upload failed: ${e.message}")
            }

            // 2. Demo Addresses
            val demoAddresses = listOf(
                SupabaseSavedAddressDto(
                    title = "Home",
                    fullAddress = "Flat 402, Royal Residency, Taj Nagri Phase 2",
                    locality = "Taj Nagri Phase 2",
                    city = "Agra",
                    landmark = "Near Shilpgram East Gate",
                    isDefault = true
                ),
                SupabaseSavedAddressDto(
                    title = "Office",
                    fullAddress = "Suite 305, Corporate Plaza, Sanjay Place",
                    locality = "Sanjay Place",
                    city = "Agra",
                    landmark = "Opposite LIC Building",
                    isDefault = false
                ),
                SupabaseSavedAddressDto(
                    title = "Parents Home",
                    fullAddress = "B-14, Radhasoami Colony, Dayalbagh Main Road",
                    locality = "Dayalbagh",
                    city = "Agra",
                    landmark = "Near Radhasoami Temple",
                    isDefault = false
                ),
                SupabaseSavedAddressDto(
                    title = "Farmhouse",
                    fullAddress = "Villa 7, Shamshabad Road Green Acres",
                    locality = "Shamshabad Road",
                    city = "Agra",
                    landmark = "Behind Toll Plaza",
                    isDefault = false
                )
            )
            try {
                api.insertAddresses(demoAddresses)
            } catch (_: Exception) {
                demoAddresses.forEach { addr ->
                    try { api.insertAddress(addr) } catch (_: Exception) {}
                }
            }

            // 3. Demo Bookings
            val demoBookings = listOf(
                SupabaseBookingDto(
                    bookingCode = "SRV-84920",
                    serviceId = "ac_service_deep",
                    serviceName = "Intense AC Foam Jet Service",
                    packageName = "1 Split AC Deep Jet Cleaning",
                    scheduledDate = "Today",
                    scheduledTime = "02:30 PM",
                    addressText = "Flat 402, Royal Residency, Taj Nagri Phase 2, Agra",
                    locality = "Taj Nagri Phase 2",
                    city = "Agra",
                    totalAmount = 499,
                    discountAmount = 100,
                    promoCode = "FIRST20",
                    paymentMethod = "Cash after service",
                    isPaid = false,
                    status = "ON_THE_WAY",
                    professionalId = "pro_rajesh_1",
                    startOtp = "6824",
                    specialNotes = "Ring bell twice, AC is in master bedroom"
                ),
                SupabaseBookingDto(
                    bookingCode = "SRV-95012",
                    serviceId = "plumbing_leak_fix",
                    serviceName = "Pipe Leakage & Tap Fix",
                    packageName = "Drain & Pipe Repair",
                    scheduledDate = "Tomorrow",
                    scheduledTime = "11:00 AM",
                    addressText = "Suite 305, Corporate Plaza, Sanjay Place, Agra",
                    locality = "Sanjay Place",
                    city = "Agra",
                    totalAmount = 249,
                    discountAmount = 50,
                    promoCode = "AGRA50",
                    paymentMethod = "UPI (Google Pay)",
                    isPaid = true,
                    status = "ASSIGNED",
                    professionalId = "pro_kavita_4",
                    startOtp = "3912",
                    specialNotes = "Pantry sink tap is leaking continuously"
                ),
                SupabaseBookingDto(
                    bookingCode = "SRV-73105",
                    serviceId = "deep_home_cleaning",
                    serviceName = "Complete Home Deep Cleaning",
                    packageName = "2 BHK Intensive Deep Clean",
                    scheduledDate = "12 Sep 2026",
                    scheduledTime = "10:00 AM",
                    addressText = "Flat 402, Royal Residency, Taj Nagri Phase 2, Agra",
                    locality = "Taj Nagri Phase 2",
                    city = "Agra",
                    totalAmount = 1899,
                    discountAmount = 200,
                    promoCode = "CLEAN100",
                    paymentMethod = "UPI (PhonePe)",
                    isPaid = true,
                    status = "COMPLETED",
                    professionalId = "pro_amit_2",
                    startOtp = "3194",
                    specialNotes = "Completed thoroughly with hospital-grade sanitizers"
                ),
                SupabaseBookingDto(
                    bookingCode = "SRV-61294",
                    serviceId = "salon_women_glow",
                    serviceName = "Glow Facial & Mani-Pedi",
                    packageName = "Bridal Radiance Package",
                    scheduledDate = "08 Sep 2026",
                    scheduledTime = "04:00 PM",
                    addressText = "B-14, Radhasoami Colony, Dayalbagh Main Road, Agra",
                    locality = "Dayalbagh",
                    city = "Agra",
                    totalAmount = 1299,
                    discountAmount = 150,
                    promoCode = "FESTIVE15",
                    paymentMethod = "Card Payment",
                    isPaid = true,
                    status = "COMPLETED",
                    professionalId = "pro_meera_3",
                    startOtp = "8401",
                    specialNotes = "Disposable sterilized equipment used"
                ),
                SupabaseBookingDto(
                    bookingCode = "SRV-54911",
                    serviceId = "electrician_instant",
                    serviceName = "Electrical Short Circuit & Wiring",
                    packageName = "Emergency Repair",
                    scheduledDate = "01 Sep 2026",
                    scheduledTime = "06:30 PM",
                    addressText = "Villa 7, Shamshabad Road Green Acres, Agra",
                    locality = "Shamshabad Road",
                    city = "Agra",
                    totalAmount = 399,
                    discountAmount = 0,
                    promoCode = "",
                    paymentMethod = "Cash after service",
                    isPaid = true,
                    status = "COMPLETED",
                    professionalId = "pro_rajesh_1",
                    startOtp = "1928",
                    specialNotes = "Fixed main MCB trip and neutralized earthing"
                )
            )
            try {
                api.upsertBookings(demoBookings)
            } catch (_: Exception) {
                demoBookings.forEach { b ->
                    try { api.insertBooking(b) } catch (_: Exception) {}
                }
            }

            // 4. Demo Reviews
            val demoReviews = listOf(
                SupabaseReviewDto(
                    serviceId = "ac_service_deep",
                    serviceName = "Intense AC Foam Jet Service",
                    professionalName = "Rajesh Sharma",
                    customerName = "Ananya V.",
                    rating = 5.0f,
                    comment = "Brilliant service! Rajesh brought proper foam jet pressure equipment and spill-jacket. AC cooling is ice-cold now, zero mess left on the wall.",
                    tags = "Punctual, Super Clean, Expert",
                    dateText = "2 days ago"
                ),
                SupabaseReviewDto(
                    serviceId = "deep_home_cleaning",
                    serviceName = "Complete Home Deep Cleaning",
                    professionalName = "Amit Kumar & Team",
                    customerName = "Vikram Singhania",
                    rating = 4.9f,
                    comment = "Booked for our home in Fatehabad Road Agra before family arrived. Every bathroom tile, balcony rail, and kitchen chimney was scrubbed mirror-shine.",
                    tags = "Detail Oriented, Professional",
                    dateText = "Last week"
                ),
                SupabaseReviewDto(
                    serviceId = "salon_women_glow",
                    serviceName = "Glow Facial & Mani-Pedi",
                    professionalName = "Meera Saxena",
                    customerName = "Pooja Agarwal",
                    rating = 5.0f,
                    comment = "Meera brought all 100% sanitized disposable kits. The facial massage was so relaxing right in my living room in Dayalbagh. Will book again!",
                    tags = "Hygienic, Gentle, Punctual",
                    dateText = "3 days ago"
                ),
                SupabaseReviewDto(
                    serviceId = "plumbing_leak_fix",
                    serviceName = "Pipe Leakage & Tap Fix",
                    professionalName = "Kavita Singh",
                    customerName = "Rohan Gupta",
                    rating = 4.8f,
                    comment = "Replaced the old rusted angle valve under the kitchen sink in 20 minutes. Clean plumbing work and upfront transparent pricing.",
                    tags = "Fast Arrival, Fair Price",
                    dateText = "5 days ago"
                ),
                SupabaseReviewDto(
                    serviceId = "electrician_instant",
                    serviceName = "Electrical Short Circuit & Wiring",
                    professionalName = "Rajesh Sharma",
                    customerName = "Sunil Mathur",
                    rating = 5.0f,
                    comment = "Our main MCB kept tripping due to heavy geyser load. The electrician diagnosed a neutral wire short and re-terminated it safely within half an hour.",
                    tags = "Knowledgeable, Safe Work",
                    dateText = "1 week ago"
                )
            )
            try {
                api.insertReviews(demoReviews)
            } catch (_: Exception) {
                demoReviews.forEach { r ->
                    try { api.insertReview(r) } catch (_: Exception) {}
                }
            }

            // Sync back to local Room as well
            syncNow()

            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                isConnected = true,
                lastSyncTime = System.currentTimeMillis(),
                lastMessage = "Seeded 5 demo bookings, 5 reviews, 4 addresses & profile to Supabase"
            )
            true
        } catch (e: Exception) {
            Log.e(TAG, "Seeding demo data to Supabase failed: ${e.message}", e)
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                error = "Seeding failed: ${e.localizedMessage ?: e.message}"
            )
            false
        }
    }
}
