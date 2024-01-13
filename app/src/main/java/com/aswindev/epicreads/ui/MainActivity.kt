package com.aswindev.epicreads.ui

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.commit
import com.aswindev.epicreads.R
import com.aswindev.epicreads.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private val discoverFragment by lazy { DiscoverFragment() }
    private val favoritesFragment by lazy { FavoritesFragment() }
    private val searchFragment by lazy { SearchFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        setupDrawerNav()
        setupBottomNav()
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
                        replace(R.id.frame_content, discoverFragment)
                    }
                }
                R.id.bottom_nav_search -> {
                    supportFragmentManager.commit {
                        replace(R.id.frame_content, searchFragment)
                    }
                }
                else -> {
                    supportFragmentManager.commit {
                        replace(R.id.frame_content, favoritesFragment)
                    }
                }
            }
            true
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