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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteBooksViewModel(application: Application) : AndroidViewModel(application) {
    private val booksLiveData = MutableLiveData<List<Book>>()
    private lateinit var database: AppDatabase
    private val bookDetailsDao by lazy {
        database = AppDatabase.getDatabase(application)
        database.getBookDetailsDao()
    }

    fun getBooks(): LiveData<List<Book>> = booksLiveData

    fun fetchBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            val allBookDetails = bookDetailsDao.getAllBooks()
            booksLiveData.postValue(convertDetailsToBooks(allBookDetails))
        }
    }

    private fun convertDetailsToBooks(allBookDetails: List<BookDetails>): List<Book> {
        val fetchedBooks = mutableListOf<Book>()

        for (bookDetails in allBookDetails) {
            fetchedBooks.add(
                Book(
                    id = bookDetails.id,
                    imageUrl = bookDetails.imageUrl ?: "",
                    title = bookDetails.title ?: "",
                    subtitle = bookDetails.subtitle ?: "",
                    isbn = bookDetails.isbn,
                    authors = bookDetails.authors ?: emptyList(),
                    notes = bookDetails.personalNotes ?: ""
                )
            )
        }
        return fetchedBooks
    }

    fun deleteBook(book: Book) {
        Log.d("FavoriteBooksViewModel", "Trying to delete Book ${book.id}")
        viewModelScope.launch(Dispatchers.IO) {
            bookDetailsDao.deleteBook(book.id)
            fetchBooks()
        }
    }

    fun updateBook(book: Book, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            bookDetailsDao.updateBook(
                BookDetails(
                    id = book.id,
                    title = book.title,
                    subtitle = book.subtitle,
                    authors = book.authors,
                    isbn = book.isbn,
                    imageUrl = book.imageUrl,
                    personalNotes = notes
                )
            )
            fetchBooks()
        }
    }
}