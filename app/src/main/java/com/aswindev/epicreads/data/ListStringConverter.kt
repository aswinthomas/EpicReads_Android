package com.aswindev.epicreads.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListStringConverter {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value == null){
            return emptyList()
        }
        val listType = object: TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toString(value: List<String>?): String {
        return Gson().toJson(value)
    }
}