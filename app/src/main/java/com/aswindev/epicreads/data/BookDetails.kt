package com.aswindev.epicreads.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "book_details")
data class BookDetails(
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    val isbn: String,
    val title: String,
    val subtitle: String?,
    @TypeConverters(ListStringConverter::class)
    val authors: List<String>?,
    val imageUrl: String?,
    val personalNotes: String?
)
