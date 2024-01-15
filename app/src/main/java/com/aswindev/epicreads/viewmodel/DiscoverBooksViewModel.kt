package com.aswindev.epicreads.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aswindev.epicreads.Book
import com.aswindev.epicreads.network.GoogleBooksService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiscoverBooksViewModel : ViewModel() {
    private val booksLiveData = MutableLiveData<List<Book>>()

    fun getBooks(): LiveData<List<Book>> = booksLiveData

    fun fetchBooks() {
        val dummyBooks = getDummyRecommendations()

        viewModelScope.launch(Dispatchers.IO) {
            val fetchedBooks = fetchBookCovers(dummyBooks)
            booksLiveData.postValue(fetchedBooks)
            Log.d("DiscoverBooksViewModel", "Fetched ${fetchedBooks.size} books")
        }
    }

    private suspend fun fetchBookCovers(books: List<Book>): List<Book> {
        val fetchedBooksDeferred = books.map { book ->
            inspectBook(book)
        }
        return fetchedBooksDeferred
    }

    private suspend fun inspectBook(book: Book): Book {
        val bookItems = GoogleBooksService.fetchBook(isbn = book.isbn)
        val bookItem = bookItems.firstOrNull()
        val title = bookItem?.volumeInfo?.title
        val coverUrl = bookItem?.volumeInfo?.imageLinks?.thumbnail
            ?: "https://covers.openlibrary.org/b/isbn/9781494563165-M.jpg"
        val secureCoverUrl = coverUrl.replace("http://", "https://")
        Log.d("DiscoverBooksViewModel", "Title: $title")
        Log.d("DiscoverBooksViewModel", "Cover url: $secureCoverUrl")
        return book.copy(imageUrl = secureCoverUrl, title = title ?: "")
    }


    private fun getDummyRecommendations(): List<Book> {
        return listOf(
            Book(isbn = "9780735211292"),
            Book(isbn = "9781607747307"),
            Book(isbn = "9781577314806"),
            Book(isbn = "9780307465351"),
            Book(isbn = "9781501111105"),
            Book(isbn = "9780062457738")
//            ,
//            Book(isbn = "9781444787011"),
//            Book(isbn = "9780307465351"),
//            Book(isbn = "9780062641540"),
//            Book(isbn = "9781444787011"),
//            Book(isbn = "9780380810338"),
//            Book(isbn = "9781451639612"),
//            Book(isbn = "9780767922715"),
//            Book(isbn = "9781401958759"),
//            Book(isbn = "9780062316110"),
//            Book(isbn = "9781444787011"),
//            Book(isbn = "9780060835910"),
//            Book(isbn = "9780307338204"),
//            Book(isbn = "9780061122415"),
//            Book(isbn = "9780671027032")
        )
    }
}