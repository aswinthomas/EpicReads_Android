package com.aswindev.epicreads.ui

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.aswindev.epicreads.viewmodel.DiscoverBooksViewModel
import com.aswindev.epicreads.databinding.FragmentDiscoverBinding

class DiscoverFragment : Fragment() {
    private lateinit var binding: FragmentDiscoverBinding
    private lateinit var viewModel: DiscoverBooksViewModel
    private lateinit var adapter: BooksAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("DiscoverFragment", "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        updateGridLayout()
        createViewModel(savedInstanceState)
    }

    private fun createViewModel(savedInstanceState: Bundle?) {
        adapter = BooksAdapter(mutableListOf(), lifecycleScope)
        binding.recyclerView.adapter = adapter
        viewModel = ViewModelProvider(this).get(DiscoverBooksViewModel::class.java)
        viewModel.getBooks().observe(viewLifecycleOwner, Observer { books ->
            adapter.update(books)
        })

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
}