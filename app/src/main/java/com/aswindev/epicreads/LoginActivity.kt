package com.aswindev.epicreads

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.aswindev.epicreads.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.buttonLogin.setOnClickListener {
            loginUser(binding.editTextEmail.text.toString().trim(), binding.editTextPassword.text.toString().trim())
        }
        binding.textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "User $email successfully logged in")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    task.exception?.let {
                        Log.d("LoginActivity", "Login failed for User $email: ${it.message}")
                        val message = it.localizedMessage?:"An unknown error occurred"
                        AlertDialog.Builder(this)
                            .setTitle("Error Logging User")
                            .setMessage(message)
                            .setNeutralButton("Ok") { dialog, which -> dialog.dismiss() }
                            .show()
                    }
                }
            }

    }
}