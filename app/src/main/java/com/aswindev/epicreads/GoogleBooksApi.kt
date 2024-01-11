package com.aswindev.epicreads

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("volumes")
    fun searchBooks(@Query("q") query: String): Call<GoogleBooksResponse>
}

data class GoogleBooksResponse(
    val items: List<BookItem>
)

data class BookItem(
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val thumbnail: String
)

