package com.example.firebaseauthenticationapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.firebaseauthenticationapp.R
import com.example.firebaseauthenticationapp.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)

        with(binding) {
            btnReset.setOnClickListener {
                val email = edtEmail.text.toString().trim()

                // Validasi inputan email
                if (email.isEmpty()) {
                    edtEmail.error = "Email Harus Diisi"
                    edtEmail.requestFocus()
                    return@setOnClickListener
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtEmail.error = "Format Email Tidak Valid"
                    edtEmail.requestFocus()
                    return@setOnClickListener
                }

                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this@ResetPasswordActivity, "Email Reset Telah Dikirim", Toast.LENGTH_SHORT).show()
                        Intent(this@ResetPasswordActivity, LoginActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                            startActivity(it)
                        }
                    } else {
                        Toast.makeText(this@ResetPasswordActivity, "${it.exception}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}