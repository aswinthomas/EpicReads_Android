package com.aswindev.epicreads

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aswindev.epicreads.databinding.ItemBookBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target


class BooksAdapter(private val books: List<Book>) : RecyclerView.Adapter<BooksAdapter.ViewHolder>() {

    override fun getItemCount() = books.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemBookBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position])
    }

    inner class ViewHolder(private val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            Glide.with(binding.imageViewBookCover.context)
                .load(book.imageUrl)
                .placeholder(android.R.drawable.ic_menu_recent_history)
                .override(Target.SIZE_ORIGINAL, 280)
                .into(binding.imageViewBookCover)
        }
    }
}

data class Book(val title: String = "",
                val isbn: String = "",
                val imageUrl: String = "",
                val authors: List<String> = listOf()
)
