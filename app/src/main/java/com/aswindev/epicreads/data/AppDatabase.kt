package com.aswindev.epicreads.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [BookDetails::class], version = 4)
@TypeConverters(ListStringConverter::class)
abstract class AppDatabase: RoomDatabase() {

    companion object {
        @Volatile
        private var DB_INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return DB_INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app-database"
                ).fallbackToDestructiveMigration()
                    .build()
                DB_INSTANCE = instance
                instance
            }
        }
    }

    abstract fun getBookDetailsDao(): BookDetailsDao

}