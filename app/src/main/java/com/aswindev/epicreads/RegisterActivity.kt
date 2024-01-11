package com.aswindev.epicreads

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.aswindev.epicreads.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.buttonRegister.setOnClickListener {
            registerUser(binding.editTextEmail.text.toString().trim(), binding.editTextPassword.text.toString().trim())
        }
        binding.buttonLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private fun registerUser(email: String, password: String) {
        if (email.isEmpty()) {
            binding.editTextEmail.error = "Email is required"
            binding.editTextEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.editTextPassword.error = "Password is required"
            binding.editTextPassword.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Log.d("RegisterActivity", "User $email successfully registered")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    task.exception?.let {
                        Log.d("RegisterActivity", "Failed to register user $email: ${it.message}")
                        val message = it.localizedMessage?:"An unknown error occurred"
                        AlertDialog.Builder(this)
                            .setTitle("Error Registering User")
                            .setMessage(message)
                            .setNeutralButton("Ok") { dialog, which -> dialog.dismiss() }
                            .show()
                    }
                }
            }
    }
}