package com.aswindev.epicreads

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aswindev.epicreads.databinding.ItemBookBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target


class BooksAdapter(private val books: List<Book>) : RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {

    class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

        Glide.with(holder.binding.imageViewBookCover.context)
            .load(book.imageUrl)
            .placeholder(R.drawable.ic_menu_recent_history)
            .override(Target.SIZE_ORIGINAL, 280)
            .into(holder.binding.imageViewBookCover)
    }

    override fun getItemCount() = books.size


}

data class Book(val title: String = "",
                val isbn: String = "",
                val imageUrl: String = "",
                val authors: List<String> = listOf()
)
