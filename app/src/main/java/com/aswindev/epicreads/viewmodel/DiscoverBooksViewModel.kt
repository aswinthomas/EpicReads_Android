package com.aswindev.epicreads.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aswindev.epicreads.ui.Book
import com.aswindev.epicreads.network.GoogleBooksService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
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
            viewModelScope.async {
                fetchBook(book)
            }
        }
        return fetchedBooksDeferred.awaitAll()
    }

    private suspend fun fetchBook(book: Book): Book {
        val bookItems = GoogleBooksService.fetchBook(query = book.title)
        val bookItem = bookItems.firstOrNull()
        val title = bookItem?.title
        val coverUrl = bookItem?.thumbnail
            ?: "https://covers.openlibrary.org/b/isbn/9781494563165-M.jpg"
        val secureCoverUrl = coverUrl.replace("http://", "https://")
        Log.d("DiscoverBooksViewModel", "Title: $title")
        Log.d("DiscoverBooksViewModel", "Cover url: $secureCoverUrl")
        return book.copy(imageUrl = secureCoverUrl, title = title ?: "")
    }


    private fun getDummyRecommendations(): List<Book> {
        return listOf(
            Book(title = "The Celestine Prophecy"),
            Book(title = "The Secret"),
            Book(title = "The Power of Positive Thinking"),
            Book(title = "Men Are from Mars, Women Are from Venus"),
            Book(title = "How to Win Friends and Influence People"),
            Book(title = "Chicken Soup for the Soul"),
            Book(title = "The Four Agreements"),
            Book(title = "The Road Less Traveled: A New Psychology of Love, Traditional Values, and Spiritual Growth"),
            Book(title = "Project Bold Life: The Proven Formula to Take on Challenges and Achieve Happiness and Success"),
            Book(title = "The Power of Colors: The Path to Self-Healing and Personal Transformation Through Native American Ancient Wisdom"),
            Book(title = "Didn't See That Coming: Putting Life Back Together When Your World Falls Apart")
        )
//        return listOf(
//            Book(isbn = "9780735211292"),
//            Book(isbn = "9781607747307"),
//            Book(isbn = "9781577314806"),
//            Book(isbn = "9780307465351"),
//            Book(isbn = "9781501111105"),
//            Book(isbn = "9780062457738")
//        )
    }
}