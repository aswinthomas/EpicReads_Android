package com.aswindev.epicreads

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.setPadding
import com.aswindev.epicreads.databinding.ActivityLoginBinding
import com.google.android.material.internal.ViewUtils.dpToPx
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        binding.textViewForgotPassword.setOnClickListener { showResetPasswordDialog() }
    }


    private fun loginUser(email: String, password: String) {
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

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "User $email successfully logged in")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    task.exception?.let {
                        Log.d("LoginActivity", "Login failed for User $email: ${it.message}")
                        val message = it.localizedMessage ?: "An unknown error occurred"
                        AlertDialog.Builder(this)
                            .setTitle("Error Logging User")
                            .setMessage(message)
                            .setNeutralButton("Ok") { dialog, which -> dialog.dismiss() }
                            .show()
                    }
                }
            }
    }

    private fun showResetPasswordDialog() {
        val emailInput = EditText(this)
        emailInput.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        emailInput.hint = "Enter your email"

        AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setView(emailInput)
            .setPositiveButton("Reset") { _, _ ->
                auth.sendPasswordResetEmail(emailInput.text.toString().trim())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Snackbar.make(binding.root, "Reset password email sent", Snackbar.LENGTH_LONG).show()
                        } else {
                            Snackbar.make(binding.root, "Failed to send reset email", Snackbar.LENGTH_LONG).show()
                        }
                    }
            }
            .show()
    }
}