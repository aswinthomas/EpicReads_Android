package com.aswindev.epicreads.network

import android.util.Log
import com.aswindev.epicreads.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

object GoogleBooksService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: GoogleBooksApiService = retrofit.create(GoogleBooksApiService::class.java)

    suspend fun fetchBook(isbn: String = "", title: String = ""): List<BookItem> {
        val queryParams = mutableListOf<String>()

        if (title.isNotEmpty()) {
            queryParams.add("intitle:$title")
        }
        if(isbn.isNotEmpty()) {
            queryParams.add("isbn:$isbn")
        }
        val query = queryParams.joinToString("+")
        val apiKey = BuildConfig.GOOGLE_BOOKS_API_KEY

        return try {
            val response = service.searchBooks(query, apiKey)
            if (response.isSuccessful) {
                response.body()?.items ?: emptyList()
            } else {
                Log.d("DiscoverBooksViewModel", "Unable to fetch from GoogleBooks ${response.toString()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("DiscoverBooksViewModel", "Error fetching book cover ${e.localizedMessage}")
            emptyList()
        }
    }
}
