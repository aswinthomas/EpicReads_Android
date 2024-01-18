package com.aswindev.epicreads.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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

    @Query("DELETE FROM book_details WHERE id = :id")
    suspend fun deleteBook(id: Int)
}