package com.example.data.remote.supabase

import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.CustomerReview
import com.example.data.model.SavedAddress
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.squareup.moshi.Json

data class SupabaseBookingDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "booking_code") val bookingCode: String,
    @Json(name = "service_id") val serviceId: String,
    @Json(name = "service_name") val serviceName: String,
    @Json(name = "package_name") val packageName: String,
    @Json(name = "scheduled_date") val scheduledDate: String,
    @Json(name = "scheduled_time") val scheduledTime: String,
    @Json(name = "address_text") val addressText: String,
    @Json(name = "locality") val locality: String,
    @Json(name = "city") val city: String = "Agra",
    @Json(name = "total_amount") val totalAmount: Int,
    @Json(name = "discount_amount") val discountAmount: Int = 0,
    @Json(name = "promo_code") val promoCode: String = "",
    @Json(name = "payment_method") val paymentMethod: String = "Cash after service",
    @Json(name = "is_paid") val isPaid: Boolean = false,
    @Json(name = "status") val status: String = "ASSIGNED",
    @Json(name = "professional_id") val professionalId: String = "pro_rajesh_1",
    @Json(name = "start_otp") val startOtp: String = "4829",
    @Json(name = "special_notes") val specialNotes: String = "",
    @Json(name = "created_at") val createdAt: Long? = null
)

fun Booking.toSupabaseDto(): SupabaseBookingDto = SupabaseBookingDto(
    id = if (id > 0) id else null,
    bookingCode = bookingCode,
    serviceId = serviceId,
    serviceName = serviceName,
    packageName = packageName,
    scheduledDate = scheduledDate,
    scheduledTime = scheduledTime,
    addressText = addressText,
    locality = locality,
    city = city,
    totalAmount = totalAmount,
    discountAmount = discountAmount,
    promoCode = promoCode,
    paymentMethod = paymentMethod,
    isPaid = isPaid,
    status = status.name,
    professionalId = professionalId,
    startOtp = startOtp,
    specialNotes = specialNotes,
    createdAt = createdAt
)

fun SupabaseBookingDto.toDomainBooking(): Booking = Booking(
    id = id ?: 0L,
    bookingCode = bookingCode,
    serviceId = serviceId,
    serviceName = serviceName,
    packageName = packageName,
    scheduledDate = scheduledDate,
    scheduledTime = scheduledTime,
    addressText = addressText,
    locality = locality,
    city = city,
    totalAmount = totalAmount,
    discountAmount = discountAmount,
    promoCode = promoCode,
    paymentMethod = paymentMethod,
    isPaid = isPaid,
    status = try {
        BookingStatus.valueOf(status)
    } catch (e: Exception) {
        BookingStatus.ASSIGNED
    },
    professionalId = professionalId,
    startOtp = startOtp,
    specialNotes = specialNotes,
    createdAt = createdAt ?: System.currentTimeMillis()
)

data class SupabaseSavedAddressDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "title") val title: String,
    @Json(name = "full_address") val fullAddress: String,
    @Json(name = "locality") val locality: String,
    @Json(name = "city") val city: String = "Agra",
    @Json(name = "landmark") val landmark: String = "",
    @Json(name = "is_default") val isDefault: Boolean = false
)

fun SavedAddress.toSupabaseDto(): SupabaseSavedAddressDto = SupabaseSavedAddressDto(
    id = if (id > 0) id else null,
    title = title,
    fullAddress = fullAddress,
    locality = locality,
    city = city,
    landmark = landmark,
    isDefault = isDefault
)

fun SupabaseSavedAddressDto.toDomainAddress(): SavedAddress = SavedAddress(
    id = id ?: 0L,
    title = title,
    fullAddress = fullAddress,
    locality = locality,
    city = city,
    landmark = landmark,
    isDefault = isDefault
)

data class SupabaseReviewDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "service_id") val serviceId: String,
    @Json(name = "service_name") val serviceName: String,
    @Json(name = "professional_name") val professionalName: String,
    @Json(name = "customer_name") val customerName: String = "Priya S.",
    @Json(name = "rating") val rating: Float = 5.0f,
    @Json(name = "comment") val comment: String,
    @Json(name = "tags") val tags: String = "Punctual, Expert",
    @Json(name = "date_text") val dateText: String = "Today",
    @Json(name = "created_at") val createdAt: Long? = null
)

fun CustomerReview.toSupabaseDto(): SupabaseReviewDto = SupabaseReviewDto(
    id = if (id > 0) id else null,
    serviceId = serviceId,
    serviceName = serviceName,
    professionalName = professionalName,
    customerName = customerName,
    rating = rating,
    comment = comment,
    tags = tags,
    dateText = dateText,
    createdAt = createdAt
)

fun SupabaseReviewDto.toDomainReview(): CustomerReview = CustomerReview(
    id = id ?: 0L,
    serviceId = serviceId,
    serviceName = serviceName,
    professionalName = professionalName,
    customerName = customerName,
    rating = rating,
    comment = comment,
    tags = tags,
    dateText = dateText,
    createdAt = createdAt ?: System.currentTimeMillis()
)

data class SupabaseUserProfileDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "email") val email: String,
    @Json(name = "city") val city: String = "Agra",
    @Json(name = "locality") val locality: String = "Taj Nagri Phase 2",
    @Json(name = "role") val role: String = "CUSTOMER"
)

fun UserProfile.toSupabaseDto(): SupabaseUserProfileDto = SupabaseUserProfileDto(
    id = id,
    name = name,
    phone = phone,
    email = email,
    city = city,
    locality = locality,
    role = role.name
)

fun SupabaseUserProfileDto.toDomainProfile(): UserProfile = UserProfile(
    id = id,
    name = name,
    phone = phone,
    email = email,
    city = city,
    locality = locality,
    role = try {
        UserRole.valueOf(role)
    } catch (e: Exception) {
        UserRole.CUSTOMER
    }
)
