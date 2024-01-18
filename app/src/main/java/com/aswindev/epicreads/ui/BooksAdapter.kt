package com.aswindev.epicreads.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aswindev.epicreads.databinding.ItemBookBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface OnBookItemClickListener {
    fun onClick(book: Book)
}

class BooksAdapter(
    private val books: MutableList<Book>,
    private val coroutineScope: CoroutineScope,
    private val onBookItemClickListener: OnBookItemClickListener? = null
) :
    RecyclerView.Adapter<BooksAdapter.ViewHolder>() {

    override fun getItemCount() = books.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemBookBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position])
        holder.itemView.setOnClickListener {
            onBookItemClickListener?.onClick(books[position])
        }
    }

    inner class ViewHolder(private val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            Glide.with(binding.imageViewBookCover.context)
                .load(book.imageUrl)
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(android.R.drawable.ic_menu_recent_history)
                .override(Target.SIZE_ORIGINAL, 280)
                .into(binding.imageViewBookCover)

        }
    }

    fun update(newBooks: List<Book>) {
        books.clear()
        books.addAll(newBooks)
        for ((index, book) in books.withIndex()) {
            Log.d("Book Adapter", "$index. ${book.title}")
        }
        notifyDataSetChanged()
        Log.d("BooksAdapter", "Got new ${books.size} books")
    }
}

data class Book(
    val id: Int = 0, //internal id in DB
    val title: String = "",
    val subtitle: String = "",
    val isbn: String = "",
    val imageUrl: String = "",
    val authors: List<String> = listOf(),
    val notes: String = ""
)