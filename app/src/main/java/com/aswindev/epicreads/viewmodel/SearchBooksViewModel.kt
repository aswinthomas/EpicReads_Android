package com.aswindev.epicreads.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aswindev.epicreads.Book
import com.aswindev.epicreads.network.BookItem
import com.aswindev.epicreads.BuildConfig
import com.aswindev.epicreads.data.AppDatabase
import com.aswindev.epicreads.data.BookDetails
import com.aswindev.epicreads.data.BookDetailsDao
import com.aswindev.epicreads.network.GoogleBooksResponse
import com.aswindev.epicreads.network.GoogleBooksService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class SearchBooksViewModel(application: Application) : AndroidViewModel(application) {
    private val booksLiveData = MutableLiveData<List<Book>>()
    private lateinit var database: AppDatabase
    private val bookDetailsDao by lazy {
        database = AppDatabase.getDatabase(application)
        database.getBookDetailsDao()
    }

    fun getBooks(): LiveData<List<Book>> = booksLiveData

    fun searchBooks(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("SearchBooksViewModel::searchBooks", "Searching books with title $title")
            val fetchedBooks = fetchBookCovers(title)
            booksLiveData.postValue(fetchedBooks)
            Log.d("SearchBooksViewModel::searchBooks", "Got ${booksLiveData.value?.size} books")
        }
    }

    private suspend fun fetchBookCovers(title: String): List<Book> {
        val fetchedBooks = mutableListOf<Book>()

        val bookItems = GoogleBooksService.fetchBook(title = title)
        for (bookItem in bookItems) {
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
        return fetchedBooks
    }

    fun insertBook(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            bookDetailsDao.createBook(
                BookDetails(
                    title = book.title,
                    subtitle = book.subtitle,
                    authors = book.authors,
                    isbn = book.isbn,
                    personalNotes = null
                )
            )
        }
    }
}