package com.aswindev.epicreads.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("volumes")
    fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): Call<GoogleBooksResponse>
}

data class GoogleBooksResponse(
    val items: List<BookItem>
)

data class BookItem(
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String?,
    val subtitle: String?,
    val authors: List<String>,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val thumbnail: String
)

