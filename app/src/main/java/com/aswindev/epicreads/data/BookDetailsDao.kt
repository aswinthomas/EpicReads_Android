package com.aswindev.epicreads.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.aswindev.epicreads.Book

@Dao
interface BookDetailsDao {

    @Insert
    suspend fun createBook(bookDetails: BookDetails)

//    @Query("SELECT * FROM book_details WHERE isbn = :isbn LIMIT 1")
//    suspend fun getBook(isbn: String): BookDetails?

    @Query("SELECT * FROM book_details")
    suspend fun getAllBooks(): List<BookDetails>

    @Update
    suspend fun updateBook(bookDetails: BookDetails)

    @Query("DELETE FROM book_details WHERE isbn = :isbn")
    suspend fun deleteBook(isbn: String)
}