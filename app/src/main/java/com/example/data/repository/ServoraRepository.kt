package com.example.data.repository

import com.example.R
import com.example.data.db.AddressDao
import com.example.data.db.BookingDao
import com.example.data.db.ReviewDao
import com.example.data.db.UserDao
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class ServoraRepository(
    private val bookingDao: BookingDao,
    private val addressDao: AddressDao,
    private val reviewDao: ReviewDao,
    private val userDao: UserDao,
    val syncManager: SupabaseSyncManager? = null
) {
    // Reactive Room Streams
    val allBookings: Flow<List<Booking>> = bookingDao.getAllBookings()
    val activeBooking: Flow<Booking?> = bookingDao.getActiveBooking()
    val savedAddresses: Flow<List<SavedAddress>> = addressDao.getAllAddresses()
    val allReviews: Flow<List<CustomerReview>> = reviewDao.getAllReviews()
    val currentUser: Flow<UserProfile?> = userDao.getCurrentUser()
    val supabaseSyncState: StateFlow<SupabaseSyncState>? = syncManager?.syncState

    fun triggerSupabaseSync() {
        syncManager?.triggerSync()
    }

    suspend fun syncSupabaseNow(): Boolean {
        return syncManager?.syncNow() ?: false
    }

    suspend fun seedDemoDataToSupabase(): Boolean {
        return syncManager?.seedDemoDataToSupabase() ?: false
    }

    fun getBookingById(id: Long): Flow<Booking?> = bookingDao.getBookingById(id)
    fun getReviewsForService(serviceId: String): Flow<List<CustomerReview>> = reviewDao.getReviewsForService(serviceId)

    suspend fun createBooking(booking: Booking): Long {
        val id = bookingDao.insertBooking(booking)
        syncManager?.onBookingCreated(booking.copy(id = id))
        return id
    }

    suspend fun updateBookingStatus(id: Long, status: BookingStatus) {
        bookingDao.updateStatus(id, status)
        // Find code to sync to Supabase
        val all = bookingDao.getAllBookingsList()
        val match = all.firstOrNull { it.id == id }
        if (match != null) {
            syncManager?.onBookingStatusUpdated(match.bookingCode, status)
        }
    }

    suspend fun cancelBooking(id: Long) = updateBookingStatus(id, BookingStatus.CANCELLED)

    suspend fun addAddress(address: SavedAddress): Long {
        val id = addressDao.insertAddress(address)
        syncManager?.onAddressAdded(address.copy(id = id))
        return id
    }

    suspend fun deleteAddress(id: Long) = addressDao.deleteAddress(id)
    suspend fun setDefaultAddress(id: Long) {
        addressDao.clearDefaults()
        addressDao.setDefaultAddress(id)
    }

    suspend fun addReview(review: CustomerReview): Long {
        val id = reviewDao.insertReview(review)
        syncManager?.onReviewAdded(review.copy(id = id))
        return id
    }

    suspend fun updateUserRole(role: UserRole) {
        userDao.updateRole(role)
        val user = userDao.getCurrentUserSync()
        if (user != null) {
            syncManager?.onUserProfileUpdated(user.copy(role = role))
        }
    }

    suspend fun updateLocation(city: String, locality: String) {
        userDao.updateLocation(city, locality)
        val user = userDao.getCurrentUserSync()
        if (user != null) {
            syncManager?.onUserProfileUpdated(user.copy(city = city, locality = locality))
        }
    }

    // In-memory catalog of Categories, Services, Professionals, Offers, and Cities
    val categories: List<ServiceCategory> = listOf(
        ServiceCategory(
            id = "cat_ac",
            name = "AC Repair",
            description = "Deep jet cleaning, gas refill, repair & installation",
            startingPrice = 499,
            iconName = "ac_unit",
            tag = "Most Booked",
            isFeatured = true
        ),
        ServiceCategory(
            id = "cat_cleaning",
            name = "Home Cleaning",
            description = "Deep cleaning, bathroom, kitchen & sofa scrubbing",
            startingPrice = 999,
            iconName = "cleaning_services",
            tag = "4.9 ★ Rating",
            isFeatured = true
        ),
        ServiceCategory(
            id = "cat_salon_w",
            name = "Salon for Women",
            description = "Facial, waxing, mani-pedi, haircut & spa at home",
            startingPrice = 399,
            iconName = "spa",
            tag = "Top Rated",
            isFeatured = true
        ),
        ServiceCategory(
            id = "cat_salon_m",
            name = "Salon for Men",
            description = "Haircut, beard styling, head massage & tan care",
            startingPrice = 249,
            iconName = "face",
            tag = "Quick Visit"
        ),
        ServiceCategory(
            id = "cat_electrician",
            name = "Electrician",
            description = "Switchboards, fans, MCB fuse, wiring & chandeliers",
            startingPrice = 199,
            iconName = "bolt",
            tag = "Under 30 mins"
        ),
        ServiceCategory(
            id = "cat_plumber",
            name = "Plumber",
            description = "Leakages, taps, flush tanks, washbasins & water pipes",
            startingPrice = 199,
            iconName = "plumbing",
            tag = "Verified"
        ),
        ServiceCategory(
            id = "cat_carpenter",
            name = "Carpenter",
            description = "Furniture repair, hinges, lock installation & drills",
            startingPrice = 249,
            iconName = "handyman"
        ),
        ServiceCategory(
            id = "cat_pest",
            name = "Pest Control",
            description = "Cockroach, termite, bed bug & mosquito control",
            startingPrice = 799,
            iconName = "pest_control"
        ),
        ServiceCategory(
            id = "cat_painting",
            name = "Painting",
            description = "Full home, waterproofing, texture wall & touch-ups",
            startingPrice = 1499,
            iconName = "format_paint"
        ),
        ServiceCategory(
            id = "cat_ro",
            name = "RO Repair",
            description = "Filter replacement, membrane service & water testing",
            startingPrice = 299,
            iconName = "water_drop"
        ),
        ServiceCategory(
            id = "cat_appliances",
            name = "Appliance Repair",
            description = "Washing machine, refrigerator, microwave & geyser",
            startingPrice = 349,
            iconName = "kitchen"
        ),
        ServiceCategory(
            id = "cat_bathroom",
            name = "Bathroom Cleaning",
            description = "Hard water stains, tile scrubbing, mirror polishing",
            startingPrice = 599,
            iconName = "shower"
        ),
        ServiceCategory(
            id = "cat_kitchen",
            name = "Kitchen Cleaning",
            description = "Degreasing chimney, tiles, cabinets & gas stove",
            startingPrice = 799,
            iconName = "countertops"
        ),
        ServiceCategory(
            id = "cat_sofa",
            name = "Sofa Cleaning",
            description = "Fabric extraction, leather polish & cushions",
            startingPrice = 399,
            iconName = "chair"
        )
    )

    val services: List<ServiceItem> = listOf(
        ServiceItem(
            id = "ac_service_deep",
            categoryId = "cat_ac",
            name = "Intense AC Foam Jet Service",
            subtitle = "2x deeper dust extraction with high-pressure water jet technology",
            startingPrice = 499,
            rating = 4.84f,
            reviewsCount = 3840,
            duration = "45 mins",
            warrantyText = "30-day cooling warranty",
            description = "Service Assist's signature split and window AC deep servicing. Uses specialized indoor foam spray and high-pressure jet pump with spill-guard bag to wash indoor coils, blower fan, drain tray, and outdoor condenser.",
            imageDrawableRes = R.drawable.img_ac_repair,
            isPopular = true,
            isHeroFeatured = false,
            whatIsIncluded = listOf(
                "Complete coil & blower cleaning with high-pressure water jet",
                "Spill-proof indoor jacket jacket protection for walls & sofa",
                "Outdoor condenser coil high-pressure wash",
                "Cooling gas pressure and amp measurement check",
                "Drain pipe clearing to prevent indoor water dripping"
            ),
            whatIsNotIncluded = listOf(
                "Gas refill or leak repair (charged separately if needed)",
                "Spare parts replacement like capacitor or circuit board",
                "Dismounting/reinstalling AC unit from wall"
            ),
            packages = listOf(
                ServicePackage(
                    id = "pkg_ac_1",
                    name = "1 Split AC Jet Wash",
                    description = "Single indoor & outdoor unit comprehensive cleaning",
                    price = 499,
                    originalPrice = 699,
                    durationText = "45 mins",
                    includes = listOf("Foam Jet Indoor Wash", "Outdoor Jet Clean", "Gas Check", "Spill Guard")
                ),
                ServicePackage(
                    id = "pkg_ac_2",
                    name = "2 Split ACs Combo Pack",
                    description = "Service 2 AC units together and save ₹200",
                    price = 799,
                    originalPrice = 1198,
                    durationText = "80 mins",
                    includes = listOf("2x Foam Jet Wash", "2x Outdoor Jet Clean", "Dual Gas Diagnostics")
                ),
                ServicePackage(
                    id = "pkg_ac_3",
                    name = "Full Home Cooling (3 ACs)",
                    description = "Best value for whole apartment or bungalow",
                    price = 1149,
                    originalPrice = 1797,
                    durationText = "120 mins",
                    includes = listOf("3x Deep Wash", "Full Electrical Inspection", "Anti-Rust Spray")
                )
            ),
            faqs = listOf(
                "Will water ruin my painted wall?" to "Not at all. Our verified technicians fit a sealed waterproof protective jacket around the indoor AC that funnels all drained dirty water safely into a bucket.",
                "How often should AC jet service be done in Agra?" to "Due to Agra's seasonal dust and summer heat, servicing every 4 to 6 months maintains maximum cooling efficiency and reduces power bills."
            )
        ),
        ServiceItem(
            id = "deep_home_cleaning",
            categoryId = "cat_cleaning",
            name = "Full Home Deep Cleaning",
            subtitle = "Thorough sanitization, machine scrubbing & corner-to-corner detailing",
            startingPrice = 999,
            rating = 4.92f,
            reviewsCount = 2410,
            duration = "4 - 6 hours",
            warrantyText = "100% Satisfaction or Free Reclean",
            description = "Complete revival for your living spaces. A trained crew of 2-3 professionals arrives with industrial grade single-disc floor scrubbers, vacuum extractors, non-hazardous cleaning agents, and microfiber towels.",
            imageDrawableRes = R.drawable.img_cleaning_pro,
            isPopular = true,
            isHeroFeatured = true,
            whatIsIncluded = listOf(
                "All rooms floor scrubbing with rotary machines",
                "Kitchen deep degreasing: chimney, exhaust, tiles & cabinets",
                "Bathrooms descaling, hard water stain removal & sanitization",
                "Balcony wash, ceiling cobweb removal & fan blades dusting",
                "Window glass, railings, switchboards & door frames wiped"
            ),
            whatIsNotIncluded = listOf(
                "Repainting walls or filling cement cracks",
                "Moving heavy solid wood wardrobes or fixed safes",
                "Wet cleaning inside locked private drawers without permission"
            ),
            packages = listOf(
                ServicePackage(
                    id = "pkg_clean_1bhk",
                    name = "1 BHK Full Home",
                    description = "Complete deep clean for 1 bedroom, hall, kitchen & bath",
                    price = 999,
                    originalPrice = 1499,
                    durationText = "3 hours",
                    includes = listOf("1 Bedroom", "1 Bathroom", "1 Kitchen", "Living Room", "Dry & Wet Vacuuming")
                ),
                ServicePackage(
                    id = "pkg_clean_2bhk",
                    name = "2 BHK Full Home",
                    description = "Most popular package in Agra apartments",
                    price = 1899,
                    originalPrice = 2499,
                    durationText = "4.5 hours",
                    includes = listOf("2 Bedrooms", "2 Bathrooms", "Complete Kitchen", "Balcony & Living")
                ),
                ServicePackage(
                    id = "pkg_clean_3bhk",
                    name = "3 BHK / Villa Deep Clean",
                    description = "Comprehensive team of 3 specialists with floor machine",
                    price = 2799,
                    originalPrice = 3699,
                    durationText = "6 hours",
                    includes = listOf("3 Bedrooms", "3 Bathrooms", "Deep Kitchen Scrub", "2 Balconies", "Floor Buffing")
                )
            ),
            faqs = listOf(
                "Do I need to provide buckets or cleaning liquids?" to "No, Service Assist professionals carry all required specialized cleaning agents, rotary scrubber, vacuum, and ladders.",
                "Are the cleaning chemicals safe for pets and children?" to "Yes, we exclusively utilize eco-friendly, non-toxic hospital-grade sanitizers that leave a fresh citrus scent."
            )
        ),
        ServiceItem(
            id = "salon_women_glow",
            categoryId = "cat_salon_w",
            name = "Glow Facial & Spa at Home",
            subtitle = "Luxe skincare treatment with single-use sealed kits in your bedroom",
            startingPrice = 399,
            rating = 4.95f,
            reviewsCount = 4120,
            duration = "60 mins",
            warrantyText = "100% Sealed Monodose Kits",
            description = "Pamper yourself with salon luxury without stepping out into Agra traffic. Our certified beauticians arrive with sanitized bed sheets, portable ring lights, disposable gowns, and premium organic skincare lines.",
            imageDrawableRes = R.drawable.img_salon_wellness,
            isPopular = true,
            isHeroFeatured = false,
            whatIsIncluded = listOf(
                "Deep pore cleansing, steam extraction & walnut exfoliator",
                "Relaxing 20-minute acupressure face, neck & shoulder massage",
                "Skin brightening peel-off mask & hydration toner",
                "Disposable bed roll, towel, headband & spatula"
            ),
            whatIsNotIncluded = listOf(
                "Hair coloring or permanent straightening (book separately)",
                "Medical dermatological procedures"
            ),
            packages = listOf(
                ServicePackage(
                    id = "pkg_salon_w1",
                    name = "Fruit Radiance Facial",
                    description = "Instant glow with antioxidant fruit extracts",
                    price = 399,
                    originalPrice = 599,
                    durationText = "45 mins",
                    includes = listOf("Cleansing", "Steam", "Massage", "Pack", "Lip Balm")
                ),
                ServicePackage(
                    id = "pkg_salon_w2",
                    name = "O3+ Bridal Glow Luxury Facial",
                    description = "Deep tan removal, diamond polish & youth boost",
                    price = 999,
                    originalPrice = 1499,
                    durationText = "75 mins",
                    includes = listOf("O3+ Radiance Kit", "Neck Therapy", "Under-Eye Treatment", "Ice Roller")
                )
            ),
            faqs = listOf(
                "How do you ensure hygiene?" to "All tools are UV-sterilized and every kit is opened directly in front of you from factory-sealed packs."
            )
        ),
        ServiceItem(
            id = "electrician_quick_fix",
            categoryId = "cat_electrician",
            name = "Electrician On-Demand Visit",
            subtitle = "Certified wiremen for switches, fuse trips, fans & short circuits",
            startingPrice = 199,
            rating = 4.81f,
            reviewsCount = 1890,
            duration = "30 mins",
            warrantyText = "30-Day Workmanship Guarantee",
            description = "Get an experienced ITI-certified electrician at your doorstep in under 30 minutes in Agra. Equipped with digital multimeters, insulated safety gear, and spare testing lamps.",
            imageDrawableRes = R.drawable.img_hero_service,
            isPopular = true,
            isHeroFeatured = false,
            whatIsIncluded = listOf(
                "Doorstep diagnostic visit & fault inspection",
                "Fixing up to 2 switches / sockets or ceiling fan regulator",
                "MCB breaker trip diagnosis",
                "Safety voltage & earthing check"
            ),
            whatIsNotIncluded = listOf(
                "Cost of replacement switches, fans or MCBs (billed on actual MRP)",
                "Concealed wall cutting / whole house rewiring"
            ),
            packages = listOf(
                ServicePackage(
                    id = "pkg_elec_1",
                    name = "Quick Repair (Up to 30 mins)",
                    description = "Fix 1-2 faulty switches, sockets, or fan regulator",
                    price = 199,
                    originalPrice = 299,
                    durationText = "30 mins",
                    includes = listOf("Diagnosis", "Switch/Socket Replacement", "Safety Check")
                ),
                ServicePackage(
                    id = "pkg_elec_2",
                    name = "Fan / Chandelier Installation",
                    description = "Mounting and secure wiring for heavy ceiling fixtures",
                    price = 299,
                    originalPrice = 399,
                    durationText = "45 mins",
                    includes = listOf("Ceiling Hook Check", "Wiring Connection", "Balance Tuning")
                )
            )
        ),
        ServiceItem(
            id = "plumber_leak_fix",
            categoryId = "cat_plumber",
            name = "Plumbing Leakage & Tap Care",
            subtitle = "Fast fix for dripping taps, flush cisterns, basin chokes & pipes",
            startingPrice = 199,
            rating = 4.79f,
            reviewsCount = 1530,
            duration = "30 mins",
            warrantyText = "30-Day Leak-Free Guarantee",
            description = "Tackle frustrating water leaks and drain blockages immediately. Our verified plumbers arrive with pipe wrenches, teflon seals, silicone gaskets, and unblocking snakes.",
            imageDrawableRes = R.drawable.img_hero_service,
            isPopular = true,
            whatIsIncluded = listOf(
                "Diagnostic inspection of pipes & valves",
                "Repair/replacement of up to 2 taps or angle valves",
                "Drain unblocking for kitchen sink or washbasin trap",
                "Pressure test to ensure zero residual drip"
            ),
            whatIsNotIncluded = listOf(
                "Cost of branded brass faucets or PVC pipes (customer provides or pro procures at MRP)",
                "Excavating underground sewer lines"
            ),
            packages = listOf(
                ServicePackage(
                    id = "pkg_plumb_1",
                    name = "Tap & Leak Repair",
                    description = "Fix 1-2 dripping faucets or loose pipe joints",
                    price = 199,
                    originalPrice = 299,
                    durationText = "30 mins",
                    includes = listOf("Teflon sealing", "Washer replacement", "Flow check")
                ),
                ServicePackage(
                    id = "pkg_plumb_2",
                    name = "Toilet Flush Tank Overhaul",
                    description = "Fix continuous overflow or broken flush siphon",
                    price = 349,
                    originalPrice = 499,
                    durationText = "45 mins",
                    includes = listOf("Ball valve tuning", "Inlet replacement", "Leak test")
                )
            )
        ),
        ServiceItem(
            id = "bathroom_deep_scrub",
            categoryId = "cat_bathroom",
            name = "Intense Bathroom Stain Removal",
            subtitle = "Remove stubborn yellow hard water scales, soap scum & grout mildew",
            startingPrice = 599,
            rating = 4.88f,
            reviewsCount = 2890,
            duration = "60 mins",
            warrantyText = "Spotless Guarantee",
            description = "Make ceramic tiles and chrome fixtures sparkle like new. High-potency acid-free descaling agents break down minerals without harming expensive marble or grouting.",
            imageDrawableRes = R.drawable.img_cleaning_pro,
            isPopular = true,
            whatIsIncluded = listOf(
                "Full tile wall & floor machine scrubbing",
                "Toilet pot internal descaling & rim disinfection",
                "Mirror, glass partition & washbasin gleaming polish",
                "Chrome taps and shower head lime-scale removal"
            ),
            whatIsNotIncluded = listOf(
                "Replacing broken tiles or regrouting missing cement"
            ),
            packages = listOf(
                ServicePackage(
                    id = "pkg_bath_1",
                    name = "1 Bathroom Deep Cleaning",
                    description = "Single bathroom thorough descaling and sanitation",
                    price = 599,
                    originalPrice = 799,
                    durationText = "60 mins",
                    includes = listOf("Tiles Scrubbing", "Sanitary Descaling", "Chrome Polish")
                ),
                ServicePackage(
                    id = "pkg_bath_2",
                    name = "2 Bathrooms Combo",
                    description = "Service master and guest bathrooms together",
                    price = 999,
                    originalPrice = 1499,
                    durationText = "100 mins",
                    includes = listOf("2 Bathrooms Complete", "Exhaust Fan Clean", "Drain Disinfection")
                )
            )
        )
    )

    val professionals: List<Professional> = listOf(
        Professional(
            id = "pro_rajesh_1",
            name = "Rajesh Sharma",
            phone = "+91 98370 12894",
            specialty = "Master AC & Appliance Technician",
            rating = 4.92f,
            reviewsCount = 840,
            completedJobs = 1240,
            experienceYears = 8,
            isVerified = true,
            etaMinutes = 18,
            avatarInitials = "RS"
        ),
        Professional(
            id = "pro_amit_2",
            name = "Amit Kumar & Crew",
            phone = "+91 94122 83719",
            specialty = "Deep Home & Floor Care Specialist",
            rating = 4.89f,
            reviewsCount = 1120,
            completedJobs = 1860,
            experienceYears = 6,
            isVerified = true,
            etaMinutes = 25,
            avatarInitials = "AK"
        ),
        Professional(
            id = "pro_meera_3",
            name = "Meera Saxena",
            phone = "+91 87550 49201",
            specialty = "Certified Beautician & Skin Aesthetician",
            rating = 4.98f,
            reviewsCount = 650,
            completedJobs = 940,
            experienceYears = 7,
            isVerified = true,
            etaMinutes = 20,
            avatarInitials = "MS"
        ),
        Professional(
            id = "pro_dinesh_4",
            name = "Dinesh Verma",
            phone = "+91 97600 38291",
            specialty = "Licensed Residential Electrician",
            rating = 4.85f,
            reviewsCount = 980,
            completedJobs = 1520,
            experienceYears = 10,
            isVerified = true,
            etaMinutes = 14,
            avatarInitials = "DV"
        )
    )

    val offers: List<Offer> = listOf(
        Offer(
            code = "FIRST20",
            title = "20% OFF First Booking",
            discountDescription = "Get 20% instant discount up to ₹200 on any service for new customers in Agra.",
            percentageDiscount = 20,
            minOrderAmount = 399,
            validUntil = "30 Sep 2026"
        ),
        Offer(
            code = "CLEAN100",
            title = "₹100 Flat OFF Deep Cleaning",
            discountDescription = "Special festive discount on full home, kitchen or bathroom deep cleaning services.",
            flatDiscount = 100,
            minOrderAmount = 899,
            validUntil = "15 Oct 2026",
            categoryRestriction = "cat_cleaning"
        ),
        Offer(
            code = "GLOW50",
            title = "Flat ₹150 OFF Salon & Spa",
            discountDescription = "Pamper yourself with premium at-home facial & grooming rituals.",
            flatDiscount = 150,
            minOrderAmount = 699,
            validUntil = "31 Oct 2026",
            categoryRestriction = "cat_salon_w"
        )
    )

    val supportedCities = listOf("Agra", "Mathura", "Delhi NCR", "Noida", "Gurgaon", "Jaipur", "Lucknow")
    val agraLocalities = listOf(
        "Taj Nagri Phase 2",
        "Fatehabad Road",
        "Sanjay Place Commercial Hub",
        "Dayalbagh",
        "Kamla Nagar",
        "Sadar Bazaar & Cantt",
        "Khandari",
        "Sikandra",
        "Trans Yamuna Colony",
        "Shahganj"
    )

    fun getServiceById(id: String): ServiceItem? = services.find { it.id == id }
    fun getServicesByCategory(catId: String): List<ServiceItem> = services.filter { it.categoryId == catId }
    fun getCategoryById(id: String): ServiceCategory? = categories.find { it.id == id }
    fun getProfessionalById(id: String): Professional? = professionals.find { it.id == id }
}
