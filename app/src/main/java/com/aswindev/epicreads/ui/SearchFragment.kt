package com.aswindev.epicreads.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.aswindev.epicreads.Book
import com.aswindev.epicreads.BooksAdapter
import com.aswindev.epicreads.databinding.FragmentSearchBinding
import com.aswindev.epicreads.viewmodel.SearchBooksViewModel

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
        setTextInputListener()

        adapter = BooksAdapter(emptyList())
        binding.recyclerView.adapter = adapter
        viewModel = ViewModelProvider(this).get(SearchBooksViewModel::class.java)
        viewModel.getBooks().observe(viewLifecycleOwner, Observer { books ->
            adapter.update(books)
        })
    }

    private fun setTextInputListener() {
        binding.editTextSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchBooks(v.text.toString())
                hideKeyboard()
                true
            } else {
                false
            }
        }
//        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                // nothing to implement
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                viewModel.searchBooks(s.toString())
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                // nothing to implement
//            }
//
//        })
    }

    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun updateGridLayout() {
        val spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            else -> 4
        }
        binding.recyclerView.layoutManager = GridLayoutManager(context, spanCount)
    }
}