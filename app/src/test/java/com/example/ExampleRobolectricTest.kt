package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.ServoraDatabase
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.remote.supabase.*
import com.example.data.repository.ServoraRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Service Assist", appName)
    }

    @Test
    fun `repository has verified services and categories in Agra`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = ServoraDatabase.getDatabase(context, kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO))
        val repository = ServoraRepository(
            bookingDao = database.bookingDao(),
            addressDao = database.addressDao(),
            reviewDao = database.reviewDao(),
            userDao = database.userDao()
        )

        assertTrue(repository.categories.isNotEmpty())
        assertTrue(repository.services.isNotEmpty())
        assertTrue(repository.agraLocalities.contains("Taj Nagri Phase 2"))

        val acService = repository.services.find { it.id == "ac_service_deep" }
        assertNotNull(acService)
        assertEquals("Intense AC Foam Jet Service", acService?.name)
    }

    @Test
    fun `supabase dto conversion roundtrip preserves integrity`() {
        val domainBooking = Booking(
            id = 42L,
            bookingCode = "SRV-TEST-99",
            serviceId = "ac_service_deep",
            serviceName = "Intense AC Foam Jet Service",
            packageName = "1.5 Ton Split AC",
            scheduledDate = "Tomorrow",
            scheduledTime = "11:00 AM",
            addressText = "Taj Nagri Phase 2, Agra",
            locality = "Taj Nagri Phase 2",
            city = "Agra",
            totalAmount = 599,
            discountAmount = 50,
            promoCode = "AGRAFIRST",
            paymentMethod = "Cash after service",
            isPaid = false,
            status = BookingStatus.ASSIGNED,
            professionalId = "pro_rajesh_1",
            startOtp = "1234",
            specialNotes = "Ring doorbell twice"
        )

        val dto = domainBooking.toSupabaseDto()
        assertEquals("SRV-TEST-99", dto.bookingCode)
        assertEquals(599, dto.totalAmount)
        assertEquals("ASSIGNED", dto.status)

        val restored = dto.toDomainBooking()
        assertEquals(domainBooking.bookingCode, restored.bookingCode)
        assertEquals(domainBooking.totalAmount, restored.totalAmount)
        assertEquals(domainBooking.status, restored.status)
        assertEquals(domainBooking.addressText, restored.addressText)
    }

    @Test
    fun `supabase address and review dto conversions work properly`() {
        val review = com.example.data.model.CustomerReview(
            id = 1L,
            serviceId = "ac_service_deep",
            serviceName = "Intense AC Foam Jet Service",
            professionalName = "Rajesh Sharma",
            customerName = "Ananya V.",
            rating = 5.0f,
            comment = "Great service!",
            tags = "Fast, Clean",
            dateText = "Today"
        )
        val reviewDto = review.toSupabaseDto()
        assertEquals("Ananya V.", reviewDto.customerName)
        assertEquals(5.0f, reviewDto.rating)

        val restoredReview = reviewDto.toDomainReview()
        assertEquals("Great service!", restoredReview.comment)

        val address = com.example.data.model.SavedAddress(
            id = 5L,
            title = "Home",
            fullAddress = "Flat 402, Taj Nagri, Agra",
            locality = "Taj Nagri",
            city = "Agra",
            landmark = "Shilpgram",
            isDefault = true
        )
        val addressDto = address.toSupabaseDto()
        assertEquals("Home", addressDto.title)
        val restoredAddress = addressDto.toDomainAddress()
        assertEquals("Flat 402, Taj Nagri, Agra", restoredAddress.fullAddress)
    }
}

