package com.aswindev.epicreads

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: GoogleBooksApiService = retrofit.create(GoogleBooksApiService::class.java)
}
