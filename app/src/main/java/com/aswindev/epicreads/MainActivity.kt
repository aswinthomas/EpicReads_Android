package com.aswindev.epicreads

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aswindev.epicreads.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        // check if user is signed in
        // FirebaseAuth.getInstance().signOut()
        val currUser = FirebaseAuth.getInstance().currentUser
        Log.d("MainActivity", "Current User: $currUser")
        if (currUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}