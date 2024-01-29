package com.aswindev.epicreads.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aswindev.epicreads.ui.Book
import com.aswindev.epicreads.data.AppDatabase
import com.aswindev.epicreads.data.BookDetails
import com.aswindev.epicreads.network.GoogleBooksService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            Log.d("SearchBooksViewModel::searchBooks", "Searching books with query $title")
            val fetchedBooks = fetchBookCovers(title)
            booksLiveData.postValue(fetchedBooks)
            Log.d("SearchBooksViewModel::searchBooks", "Got ${booksLiveData.value?.size} books")
        }
    }

    private suspend fun fetchBookCovers(title: String): List<Book> {
        val fetchedBooks = mutableListOf<Book>()

        val bookItems = GoogleBooksService.fetchBook(query = title)
        for (bookItem in bookItems) {
            val coverUrl = bookItem.thumbnail
                ?: "https://covers.openlibrary.org/b/isbn/9781494563165-M.jpg"
            val secureCoverUrl = coverUrl.replace("http://", "https://")
            Log.d("SearchBooksViewModel", "Title: $title")
            Log.d("SearchBooksViewModel", "Cover url: $secureCoverUrl")
            fetchedBooks.add(
                Book(
                    imageUrl = secureCoverUrl,
                    title = bookItem.title ?: "",
                    subtitle = bookItem.subtitle ?: "",
                    authors = bookItem.authors ?: emptyList()
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
                    imageUrl = book.imageUrl,
                    personalNotes = null
                )
            )
        }
    }
}