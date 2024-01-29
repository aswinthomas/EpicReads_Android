package com.aswindev.epicreads.network


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("search-books")
    suspend fun searchBooks(
        @Query("query") query: String,
        //@Query("key") apiKey: String
    ): Response<GoogleBooksResponse>
}

data class GoogleBooksResponse(
    val items: List<BookItem>
)

data class BookItem(
    val id: String?,
    val title: String?,
    val subtitle: String?,
    val description: String?,
    val publisher: String?,
    val published_date: String?,
    val authors: List<String>?,
    val page_count: String?,
    val language: String?,
    val thumbnail: String?
)

//data class BookItem(
//    val volumeInfo: VolumeInfo
//)
//
//data class VolumeInfo(
//    val title: String?,
//    val subtitle: String?,
//    val authors: List<String>?,
//    val imageLinks: ImageLinks?
//)
//
//data class ImageLinks(
//    val thumbnail: String
//)

