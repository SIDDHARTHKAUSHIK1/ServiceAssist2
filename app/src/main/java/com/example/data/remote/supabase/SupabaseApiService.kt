package com.example.data.remote.supabase

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApiService {

    // -------------------------------------------------------------
    // BOOKINGS
    // -------------------------------------------------------------
    @GET("bookings?select=*&order=created_at.desc")
    suspend fun getBookings(): Response<List<SupabaseBookingDto>>

    @POST("bookings")
    suspend fun insertBooking(
        @Body booking: SupabaseBookingDto,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<SupabaseBookingDto>>

    @POST("bookings")
    suspend fun upsertBookings(
        @Body bookings: List<SupabaseBookingDto>,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates,return=representation"
    ): Response<List<SupabaseBookingDto>>

    @PATCH("bookings")
    suspend fun updateBookingStatus(
        @Query("booking_code") bookingCodeFilter: String, // e.g. eq.SRV-12345
        @Body updates: Map<String, String>,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<SupabaseBookingDto>>

    @PATCH("bookings")
    suspend fun updateBookingStatusById(
        @Query("id") idFilter: String, // e.g. eq.12
        @Body updates: Map<String, String>,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<SupabaseBookingDto>>

    // -------------------------------------------------------------
    // ADDRESSES
    // -------------------------------------------------------------
    @GET("saved_addresses?select=*&order=id.asc")
    suspend fun getAddresses(): Response<List<SupabaseSavedAddressDto>>

    @POST("saved_addresses")
    suspend fun insertAddress(
        @Body address: SupabaseSavedAddressDto,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<SupabaseSavedAddressDto>>

    @POST("saved_addresses")
    suspend fun insertAddresses(
        @Body addresses: List<SupabaseSavedAddressDto>,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates,return=representation"
    ): Response<List<SupabaseSavedAddressDto>>

    @DELETE("saved_addresses")
    suspend fun deleteAddress(
        @Query("id") idFilter: String // e.g. eq.5
    ): Response<Unit>

    // -------------------------------------------------------------
    // REVIEWS
    // -------------------------------------------------------------
    @GET("reviews?select=*&order=created_at.desc")
    suspend fun getReviews(): Response<List<SupabaseReviewDto>>

    @POST("reviews")
    suspend fun insertReview(
        @Body review: SupabaseReviewDto,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<SupabaseReviewDto>>

    @POST("reviews")
    suspend fun insertReviews(
        @Body reviews: List<SupabaseReviewDto>,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates,return=representation"
    ): Response<List<SupabaseReviewDto>>

    // -------------------------------------------------------------
    // USER PROFILES
    // -------------------------------------------------------------
    @GET("user_profiles?select=*")
    suspend fun getUserProfiles(): Response<List<SupabaseUserProfileDto>>

    @POST("user_profiles")
    suspend fun upsertUserProfile(
        @Body profile: SupabaseUserProfileDto,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates,return=representation"
    ): Response<List<SupabaseUserProfileDto>>
}
