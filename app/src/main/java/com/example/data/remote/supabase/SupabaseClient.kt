package com.example.data.remote.supabase

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseClient {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val authInterceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val key = SupabaseConfig.anonKey

        val requestBuilder = original.newBuilder()
            .header("apikey", key)
            .header("Authorization", "Bearer $key")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")

        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    val apiService: SupabaseApiService? by lazy {
        val rawUrl = SupabaseConfig.projectUrl
        if (!SupabaseConfig.isConfigured || rawUrl.isBlank()) {
            null
        } else {
            try {
                val baseUrl = if (rawUrl.endsWith("/")) "${rawUrl}rest/v1/" else "$rawUrl/rest/v1/"
                Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                    .create(SupabaseApiService::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Checks whether the Supabase instance is reachable with current credentials.
     * Returns Pair(Boolean isSuccess, String message)
     */
    suspend fun testConnection(): Pair<Boolean, String> {
        if (!SupabaseConfig.isConfigured) {
            return Pair(false, "Supabase credentials not configured in AI Studio Secrets panel or .env")
        }
        val service = apiService ?: return Pair(false, "Could not initialize Supabase API service")
        return try {
            val response = service.getBookings()
            if (response.isSuccessful) {
                Pair(true, "Successfully connected to Supabase PostgREST database (HTTP ${response.code()})")
            } else {
                Pair(false, "Supabase returned error: HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Pair(false, "Connection error: ${e.localizedMessage ?: e.message}")
        }
    }
}
