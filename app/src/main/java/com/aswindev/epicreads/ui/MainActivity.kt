package com.aswindev.epicreads.ui

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.aswindev.epicreads.Book
import com.aswindev.epicreads.BooksAdapter
import com.aswindev.epicreads.BooksViewModel
import com.aswindev.epicreads.R
import com.aswindev.epicreads.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewModel: BooksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        setupDrawerItems()
        viewModel = ViewModelProvider(this).get(BooksViewModel::class.java)
        viewModel.getBooks().observe(this, Observer { books ->
            populateRecommendations(books)
        })

        if (savedInstanceState == null) {
            viewModel.fetchBooks()
        }
    }

    private fun populateRecommendations(books: List<Book>) {
        if (books.isEmpty()) {
            binding.textViewWelcomeTitle.setText("Welcome to EpicReads")
            binding.recyclerView.visibility = View.GONE
            binding.textViewNoBooks.visibility = View.VISIBLE
        } else {
            binding.textViewWelcomeTitle.setText("Your Recommendations")
            binding.recyclerView.visibility = View.VISIBLE
            binding.textViewNoBooks.visibility = View.GONE
            val spanCount = when (resources.configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 2
                else -> 4
            }
            binding.recyclerView.layoutManager = GridLayoutManager(this, spanCount)
            binding.recyclerView.adapter = BooksAdapter(books)
        }
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggle
        toggle.onConfigurationChanged(newConfig)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.mainToolbarHome)
        supportActionBar?.title = getString(R.string.app_name)
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.mainToolbarHome,
            R.string.drawer_open, R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupDrawerItems() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_sign_out -> {
                    signOut()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currUser = FirebaseAuth.getInstance().currentUser
        Log.d("MainActivity", "Current User: $currUser")
        if (currUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


}