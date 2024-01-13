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

class RecommendationBooksViewModel : ViewModel() {
    private val booksLiveData = MutableLiveData<List<Book>>()

    fun getBooks(): LiveData<List<Book>> = booksLiveData

    fun fetchBooks() {
        val dummyBooks = getDummyRecommendations()
        fetchBookCovers(dummyBooks) { fetchedBooks ->
            booksLiveData.value = fetchedBooks
        }
        Log.d("RecommendationBooksViewModel", "Fetched ${booksLiveData.value?.size} books")
    }

    private fun fetchBookCovers(books: List<Book>, callback: (List<Book>) -> Unit) {
        val fetchedBooks = mutableMapOf<Int, Book>()
        var fetchCounter = 0

        books.forEachIndexed { index, book ->
            fetchBookCover(book.isbn) { bookItems ->
                val bookItem = bookItems.firstOrNull()
                val title = bookItem?.volumeInfo?.title
                val coverUrl = bookItem?.volumeInfo?.imageLinks?.thumbnail
                    ?: "https://covers.openlibrary.org/b/isbn/9781494563165-M.jpg"
                val secureCoverUrl = coverUrl.replace("http://", "https://")
                val updatedBook = book.copy(imageUrl = secureCoverUrl, title = title?:"")
                Log.d("SearchBooksViewModel", "Title: $title")
                Log.d("SearchBooksViewModel", "Cover url: $secureCoverUrl")

                fetchedBooks[index] = updatedBook
                fetchCounter++

                if (fetchCounter == books.size) {
                    val sortedBooks = fetchedBooks.toSortedMap().values.toList()
                    callback(sortedBooks)
                }
            }
        }
    }

    private fun fetchBookCover(isbn: String, callback: (List<BookItem>) -> Unit) {
        val query = "isbn:$isbn"
        val apiKey = BuildConfig.GOOGLE_BOOKS_API_KEY
        RetrofitClient.service.searchBooks(query, apiKey).enqueue(object : Callback<GoogleBooksResponse> {
            override fun onResponse(call: Call<GoogleBooksResponse>, response: Response<GoogleBooksResponse>) {
                if (response.isSuccessful) {
                    val bookItems = response.body()?.items ?: emptyList()
                    callback(bookItems)
                } else {
                    Log.d("RecommendationBooksViewModel", "Unable to fetch from GoogleBooks ${response.toString()}")
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<GoogleBooksResponse>, t: Throwable) {
                // Handle network failure or other errors
                callback(emptyList())
            }
        })
    }

    private fun getDummyRecommendations(): List<Book> {
        return listOf(
            Book(isbn = "9780735211292"),
            Book(isbn = "9781607747307"),
            Book(isbn = "9781577314806"),
            Book(isbn = "9780307465351"),
            Book(isbn = "9781501111105"),
            Book(isbn = "9780062457738"),
            Book(isbn = "9781444787011"),
            Book(isbn = "9780307465351"),
            Book(isbn = "9780062641540"),
            Book(isbn = "9781444787011"),
            Book(isbn = "9780380810338"),
            Book(isbn = "9781451639612"),
            Book(isbn = "9780767922715"),
            Book(isbn = "9781401958759"),
            Book(isbn = "9780062316110"),
            Book(isbn = "9781444787011"),
            Book(isbn = "9780060835910"),
            Book(isbn = "9780307338204"),
            Book(isbn = "9780061122415"),
            Book(isbn = "9780671027032")
        )
    }
}