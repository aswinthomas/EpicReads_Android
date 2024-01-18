package com.aswindev.epicreads.ui

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.aswindev.epicreads.databinding.DialogBookDetailsBinding
import com.aswindev.epicreads.databinding.DialogSearchBinding
import com.aswindev.epicreads.databinding.FragmentSearchBinding
import com.aswindev.epicreads.viewmodel.SearchBooksViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchBooksViewModel
    private lateinit var adapter: BooksAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateGridLayout()
        setFloatingActionListener()
        createViewModel()
    }

    private fun updateGridLayout() {
        val spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            else -> 4
        }
        binding.recyclerView.layoutManager = GridLayoutManager(context, spanCount)
    }

    private fun setFloatingActionListener() {
        binding.floatingActionButton.setOnClickListener {
            val dialogBinding = DialogSearchBinding.inflate(layoutInflater)
            val dialog = BottomSheetDialog(requireContext())
            dialog.setContentView(dialogBinding.root)
            dialogBinding.editTextTitle.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.searchBooks(v.text.toString())
                    hideKeyboard()
                    dialog.dismiss()
                    true
                } else {
                    false
                }
            }
            dialog.show()
        }
    }

    private fun createViewModel() {
        val listener = onBookItemClickListener()
        adapter = BooksAdapter(mutableListOf(), lifecycleScope, listener)
        binding.recyclerView.adapter = adapter
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(SearchBooksViewModel::class.java)
        viewModel.getBooks().observe(viewLifecycleOwner, Observer { books ->
            adapter.update(books)
        })
    }

    private fun onBookItemClickListener() =
        object : OnBookItemClickListener {
            val dialogBinding = DialogBookDetailsBinding.inflate(layoutInflater)
            override fun onClick(book: Book) {
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

                // remove any parent view if exists
                val parentView = dialogBinding.root.parent as? ViewGroup
                parentView?.removeView(dialogBinding.root)

                AlertDialog.Builder(requireContext())
                    .setTitle("Book Details")
                    .setView(dialogBinding.root)
                    .setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setNeutralButton("Add to Favorites") { dialog, which ->
                        viewModel.insertBook(book)
                        dialog.dismiss()
                    }.show()
            }
        }

    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}