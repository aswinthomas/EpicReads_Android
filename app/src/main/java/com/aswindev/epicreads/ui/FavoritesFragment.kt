package com.aswindev.epicreads.ui

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.aswindev.epicreads.Book
import com.aswindev.epicreads.BooksAdapter
import com.aswindev.epicreads.OnBookItemClickListener
import com.aswindev.epicreads.databinding.DialogBookDetailsBinding
import com.aswindev.epicreads.databinding.FragmentFavoritesBinding
import com.aswindev.epicreads.viewmodel.FavoriteBooksViewModel

class FavoritesFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavoriteBooksViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateGridLayout()
        createViewModel()
        if (savedInstanceState == null) {
            viewModel.fetchBooks()
        }
    }

    private fun updateGridLayout() {
        val spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            else -> 4
        }
        binding.recyclerView.layoutManager = GridLayoutManager(context, spanCount)
    }

    private fun createViewModel() {
        val listener = onBookItemClickedListener()
        val adapter = BooksAdapter(mutableListOf(), listener)
        binding.recyclerView.adapter = adapter
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(FavoriteBooksViewModel::class.java)
        viewModel.getBooks().observe(viewLifecycleOwner, Observer { books ->
            adapter.update(books)
        })
    }

    private fun onBookItemClickedListener() =
        object : OnBookItemClickListener {
            val dialogBinding = DialogBookDetailsBinding.inflate(layoutInflater)
            override fun onClick(book: Book) {
                dialogBinding.textInputLayoutNotes.visibility = View.VISIBLE
                val title = book.title + ": " + book.subtitle
                var authors = ""
                for ((index, author) in book.authors.withIndex()) {
                    authors += author
                    if (index < book.authors.size - 1) {
                        authors += ", "
                    }
                }
                dialogBinding.textViewBookTitleValue.setText(title)
                dialogBinding.textViewAuthorsValue.setText(authors)
                dialogBinding.editTextNotes.setText(book.notes)

                // remove any parent view if exists
                val parentView = dialogBinding.root.parent as? ViewGroup
                parentView?.removeView(dialogBinding.root)

                AlertDialog.Builder(requireContext())
                    .setTitle("Book Details")
                    .setView(dialogBinding.root)
                    .setPositiveButton("Save") { dialog, which ->
                        val notes = dialogBinding.editTextNotes.text.toString()
                        viewModel.updateBook(book, notes)
                        dialog.dismiss()
                    }
                    .setNeutralButton("Remove from Favorites") { dialog, which ->
                        viewModel.deleteBook(book)
                        dialog.dismiss()
                    }.show()
            }
        }
}