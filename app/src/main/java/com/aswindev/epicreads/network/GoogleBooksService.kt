package com.aswindev.epicreads.network

import android.util.Log
import com.aswindev.epicreads.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

object GoogleBooksService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://asia-southeast1-fyndfam.cloudfunctions.net/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: GoogleBooksApiService = retrofit.create(GoogleBooksApiService::class.java)

    suspend fun fetchBook(query: String): List<BookItem> {
        return try {
            val response = service.searchBooks(query)
            if (response.isSuccessful) {
                Log.d("GoogleBooksService", "Got ${(response.body()?.items ?: emptyList()).size}")

                response.body()?.items ?: emptyList()
            } else {
                Log.d("GoogleBooksService", "Unable to fetch from GoogleBooks $response")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("GoogleBooksService", "Error fetching book cover ${e.localizedMessage}")
            emptyList()
        }
    }
}
