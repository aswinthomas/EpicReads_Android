package com.aswindev.epicreads.ui

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.commit
import com.aswindev.epicreads.BroadcastMessageCallback
import com.aswindev.epicreads.CustomBroadcastReceiver
import com.aswindev.epicreads.R
import com.aswindev.epicreads.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private val discoverFragment by lazy { DiscoverFragment() }
    private val favoritesFragment by lazy { FavoritesFragment() }
    private val searchFragment by lazy { SearchFragment() }
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        setupDrawerNav()
        setupBottomNav()
        setupReceivers()
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
        }
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    private fun setupReceivers() {
        broadcastReceiver = CustomBroadcastReceiver(object: BroadcastMessageCallback {
            override fun showMessage(str: String) {
                val snackbar = Snackbar.make(binding.fragmentContainerView, str, Snackbar.LENGTH_LONG)
                snackbar.anchorView = binding.bottomNavView
                snackbar.show()
            }

        })

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

    private fun setupDrawerNav() {
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

    private fun setupBottomNav() {
        binding.bottomNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_nav_discover -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container_view, discoverFragment)
                    }
                }
                R.id.bottom_nav_search -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container_view, searchFragment)
                    }
                }
                else -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container_view, favoritesFragment)
                    }
                }
            }
            true
        }
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.bottomNavView.visibility = View.VISIBLE
        } else {
            binding.bottomNavView.visibility = View.GONE
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