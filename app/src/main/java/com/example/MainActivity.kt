package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Booking
import com.example.data.model.ServiceItem
import com.example.data.model.ServicePackage
import com.example.data.model.UserRole
import com.example.ui.components.LocationPickerModal
import com.example.ui.components.SearchOverlay
import com.example.ui.components.ServoraBottomNav
import com.example.ui.components.ServoraNavTab
import com.example.ui.components.ServoraTopBar
import com.example.ui.screens.BookingConfirmationScreen
import com.example.ui.screens.BookingFlowScreen
import com.example.ui.screens.BookingsListScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OffersScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ServiceDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ServoraViewModel

sealed interface AppScreen {
    data object MainTabs : AppScreen
    data class ServiceDetail(val service: ServiceItem) : AppScreen
    data class BookingFlow(val service: ServiceItem, val pkg: ServicePackage?) : AppScreen
    data class BookingTracking(val bookingId: Long) : AppScreen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = androidx.activity.SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            MyApplicationTheme(darkTheme = false) {
                ServoraApp()
            }
        }
    }
}

@Composable
fun ServoraApp(viewModel: ServoraViewModel = viewModel()) {
    var currentTab by remember { mutableStateOf(ServoraNavTab.HOME) }
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.MainTabs) }
    var showLocationPicker by remember { mutableStateOf(false) }
    var showSearchOverlay by remember { mutableStateOf(false) }

    val currentUser by viewModel.currentUser.collectAsState()
    val allBookings by viewModel.allBookings.collectAsState()
    val activeBooking by viewModel.activeBooking.collectAsState()
    val savedAddresses by viewModel.savedAddresses.collectAsState()
    val allReviews by viewModel.allReviews.collectAsState()
    val filteredServices by viewModel.filteredServices.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val bookingDraft by viewModel.bookingDraft.collectAsState()
    val supabaseSyncState by viewModel.supabaseSyncState.collectAsState()

    // Handle back button behavior
    BackHandler(enabled = currentScreen !is AppScreen.MainTabs || showSearchOverlay) {
        if (showSearchOverlay) {
            showSearchOverlay = false
        } else {
            currentScreen = AppScreen.MainTabs
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                if (currentScreen is AppScreen.MainTabs && !showSearchOverlay) {
                    ServoraTopBar(
                        selectedCity = currentUser.city,
                        selectedLocality = currentUser.locality,
                        userRole = currentUser.role,
                        onLocationClick = { showLocationPicker = true },
                        onSearchClick = { showSearchOverlay = true },
                        onRoleToggleClick = {
                            val nextRole = when (currentUser.role) {
                                UserRole.CUSTOMER -> UserRole.PROFESSIONAL
                                UserRole.PROFESSIONAL -> UserRole.ADMIN
                                UserRole.ADMIN -> UserRole.CUSTOMER
                            }
                            viewModel.switchRole(nextRole)
                        }
                    )
                }
            },
            bottomBar = {
                if (currentScreen is AppScreen.MainTabs && !showSearchOverlay) {
                    val activeCount = allBookings.count {
                        it.status != com.example.data.model.BookingStatus.COMPLETED &&
                        it.status != com.example.data.model.BookingStatus.CANCELLED
                    }
                    ServoraBottomNav(
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it },
                        activeBookingsCount = activeCount
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val screen = currentScreen) {
                    is AppScreen.MainTabs -> {
                        when (currentTab) {
                            ServoraNavTab.HOME -> {
                                HomeScreen(
                                    categories = viewModel.categories,
                                    popularServices = viewModel.services,
                                    reviews = allReviews,
                                    activeBooking = activeBooking,
                                    selectedCity = currentUser.city,
                                    onCategoryClick = { cat ->
                                        viewModel.selectCategory(cat.id)
                                        currentTab = ServoraNavTab.EXPLORE
                                    },
                                    onServiceClick = { srv ->
                                        currentScreen = AppScreen.ServiceDetail(srv)
                                    },
                                    onBookService = { srv ->
                                        viewModel.startBooking(srv)
                                        currentScreen = AppScreen.BookingFlow(srv, srv.packages.firstOrNull())
                                    },
                                    onTrackBookingClick = { bookingId ->
                                        currentScreen = AppScreen.BookingTracking(bookingId)
                                    },
                                    onSearchClick = { showSearchOverlay = true },
                                    onBecomePartnerClick = {
                                        viewModel.switchRole(UserRole.PROFESSIONAL)
                                        currentTab = ServoraNavTab.PROFILE
                                    }
                                )
                            }
                            ServoraNavTab.EXPLORE -> {
                                ExploreScreen(
                                    categories = viewModel.categories,
                                    services = filteredServices,
                                    selectedCategoryId = selectedCategoryId,
                                    searchQuery = searchQuery,
                                    onCategorySelect = { catId -> viewModel.selectCategory(catId) },
                                    onSearchChange = { q -> viewModel.setSearchQuery(q) },
                                    onServiceClick = { srv ->
                                        currentScreen = AppScreen.ServiceDetail(srv)
                                    },
                                    onBookService = { srv ->
                                        viewModel.startBooking(srv)
                                        currentScreen = AppScreen.BookingFlow(srv, srv.packages.firstOrNull())
                                    }
                                )
                            }
                            ServoraNavTab.BOOKINGS -> {
                                BookingsListScreen(
                                    bookings = allBookings,
                                    onSelectBooking = { booking ->
                                        currentScreen = AppScreen.BookingTracking(booking.id)
                                    },
                                    onBookAgain = { srvId ->
                                        val srv = viewModel.services.find { it.id == srvId } ?: viewModel.services.first()
                                        viewModel.startBooking(srv)
                                        currentScreen = AppScreen.BookingFlow(srv, srv.packages.firstOrNull())
                                    },
                                    onExploreClick = {
                                        currentTab = ServoraNavTab.EXPLORE
                                    }
                                )
                            }
                            ServoraNavTab.OFFERS -> {
                                OffersScreen(
                                    offers = viewModel.offers,
                                    onApplyOfferToExplore = { offer ->
                                        viewModel.applyPromoCode(offer.code)
                                        currentTab = ServoraNavTab.EXPLORE
                                    }
                                )
                            }
                            ServoraNavTab.PROFILE -> {
                                ProfileScreen(
                                    userProfile = currentUser,
                                    savedAddresses = savedAddresses,
                                    onSwitchRole = { role -> viewModel.switchRole(role) },
                                    onLocationClick = { showLocationPicker = true },
                                    onDeleteAddress = { id -> viewModel.deleteAddress(id) },
                                    onAddNewAddress = { title, fullAddr, loc, landmark ->
                                        viewModel.addAddress(title, fullAddr, loc, landmark)
                                    },
                                    supabaseSyncState = supabaseSyncState,
                                    onSyncWithSupabase = { viewModel.triggerSupabaseSync() },
                                    onSeedSupabaseDemoData = { viewModel.seedDemoDataToSupabase() }
                                )
                            }
                        }
                    }
                    is AppScreen.ServiceDetail -> {
                        val pro = viewModel.professionals.find { it.id == screen.service.recommendedProId }
                            ?: viewModel.professionals.firstOrNull()
                        val srvReviews = allReviews.filter { it.serviceId == screen.service.id }

                        ServiceDetailScreen(
                            service = screen.service,
                            assignedPro = pro,
                            reviews = srvReviews.ifEmpty { allReviews.take(3) },
                            onBackClick = { currentScreen = AppScreen.MainTabs },
                            onBookPackage = { srv, pkg ->
                                viewModel.startBooking(srv, pkg)
                                currentScreen = AppScreen.BookingFlow(srv, pkg)
                            }
                        )
                    }
                    is AppScreen.BookingFlow -> {
                        BookingFlowScreen(
                            service = screen.service,
                            selectedPackage = screen.pkg ?: bookingDraft.selectedPackage,
                            savedAddresses = savedAddresses,
                            availableOffers = viewModel.offers,
                            appliedOffer = bookingDraft.appliedPromo,
                            selectedAddress = bookingDraft.selectedAddress,
                            selectedDate = bookingDraft.selectedDate,
                            selectedTimeSlot = bookingDraft.selectedTimeSlot,
                            paymentMethod = bookingDraft.paymentMethod,
                            onDateChange = { d -> viewModel.updateBookingDraft(date = d) },
                            onTimeSlotChange = { s -> viewModel.updateBookingDraft(timeSlot = s) },
                            onAddressChange = { a -> viewModel.updateBookingDraft(address = a) },
                            onAddNewAddress = { title, addr, loc, landmark ->
                                viewModel.addAddress(title, addr, loc, landmark)
                            },
                            onApplyPromo = { code -> viewModel.applyPromoCode(code) },
                            onRemovePromo = { viewModel.removePromoCode() },
                            onPaymentMethodChange = { p -> viewModel.updateBookingDraft(paymentMethod = p) },
                            onConfirmBooking = {
                                viewModel.confirmBooking { newId ->
                                    currentScreen = AppScreen.BookingTracking(newId)
                                }
                            },
                            onBackClick = { currentScreen = AppScreen.MainTabs }
                        )
                    }
                    is AppScreen.BookingTracking -> {
                        val booking = allBookings.find { it.id == screen.bookingId }
                            ?: viewModel.confirmedBooking.value
                            ?: allBookings.firstOrNull()

                        if (booking != null) {
                            val pro = viewModel.professionals.find { it.id == booking.professionalId }
                                ?: viewModel.professionals.firstOrNull()

                            BookingConfirmationScreen(
                                booking = booking,
                                professional = pro,
                                onAdvanceStatus = { id, st -> viewModel.advanceBookingStatus(id, st) },
                                onCancelBooking = { id -> viewModel.cancelBooking(id) },
                                onSubmitReview = { srvId, srvName, proName, rating, comment, tags ->
                                    viewModel.submitReview(srvId, srvName, proName, rating, comment, tags)
                                },
                                onBackToHome = {
                                    currentScreen = AppScreen.MainTabs
                                    currentTab = ServoraNavTab.HOME
                                }
                            )
                        } else {
                            currentScreen = AppScreen.MainTabs
                        }
                    }
                }
            }
        }

        // Search Full Overlay
        if (showSearchOverlay) {
            SearchOverlay(
                query = searchQuery,
                onQueryChange = { q -> viewModel.setSearchQuery(q) },
                results = filteredServices,
                onClose = { showSearchOverlay = false },
                onSelectService = { srv ->
                    showSearchOverlay = false
                    currentScreen = AppScreen.ServiceDetail(srv)
                },
                onBookService = { srv ->
                    showSearchOverlay = false
                    viewModel.startBooking(srv)
                    currentScreen = AppScreen.BookingFlow(srv, srv.packages.firstOrNull())
                }
            )
        }

        // Location Modal BottomSheet
        if (showLocationPicker) {
            LocationPickerModal(
                currentCity = currentUser.city,
                currentLocality = currentUser.locality,
                supportedCities = viewModel.supportedCities,
                agraLocalities = viewModel.agraLocalities,
                onDismiss = { showLocationPicker = false },
                onLocationSelected = { city, locality ->
                    viewModel.setLocation(city, locality)
                }
            )
        }
    }
}
