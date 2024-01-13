package com.aswindev.epicreads.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aswindev.epicreads.Book
import com.aswindev.epicreads.network.BookItem
import com.aswindev.epicreads.BuildConfig
import com.aswindev.epicreads.network.GoogleBooksResponse
import com.aswindev.epicreads.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchBooksViewModel : ViewModel() {
    private val booksLiveData = MutableLiveData<List<Book>>()

    fun getBooks(): LiveData<List<Book>> = booksLiveData

    fun searchBooks(query: String) {
        Log.d("SearchBooksViewModel::searchBooks", "Searching books with query $query")
        fetchBookCovers(query) { books ->
            booksLiveData.value = books
        }
        Log.d("SearchBooksViewModel::searchBooks", "Got ${booksLiveData.value?.size} books")
    }

    private fun fetchBookCovers(query: String, callback: (List<Book>) -> Unit) {
        val fetchedBooks = mutableListOf<Book>()

        fetchBookCover(query) { bookItems ->
            bookItems.forEach { bookItem ->
                val title = bookItem.volumeInfo.title
                val subtitle = bookItem.volumeInfo.subtitle
                val authors = bookItem.volumeInfo.authors
                val coverUrl = bookItem.volumeInfo.imageLinks?.thumbnail
                    ?: "https://covers.openlibrary.org/b/isbn/9781494563165-M.jpg"
                val secureCoverUrl = coverUrl.replace("http://", "https://")
                Log.d("SearchBooksViewModel", "Title: $title")
                Log.d("SearchBooksViewModel", "Cover url: $secureCoverUrl")
                fetchedBooks.add(
                    Book(
                        imageUrl = secureCoverUrl,
                        title = title ?: "",
                        subtitle = subtitle ?: "",
                        authors = authors
                    )
                )
            }
            callback(fetchedBooks)
        }
    }

    private fun fetchBookCover(queryStr: String, callback: (List<BookItem>) -> Unit) {
        val query = "intitle:$queryStr"
        val apiKey = BuildConfig.GOOGLE_BOOKS_API_KEY
        RetrofitClient.service.searchBooks(query, apiKey).enqueue(object : Callback<GoogleBooksResponse> {
            override fun onResponse(call: Call<GoogleBooksResponse>, response: Response<GoogleBooksResponse>) {
                if (response.isSuccessful) {
                    val bookItems = response.body()?.items ?: emptyList()
                    Log.d("SearchBooksViewModel::fetchBookCover", "Got ${bookItems.size} books")
                    callback(bookItems)
                } else {
                    Log.d("SearchBooksViewModel", "Unable to fetch from Google Books ${response.toString()}")
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<GoogleBooksResponse>, t: Throwable) {
                callback(emptyList())
            }
        })
    }
}