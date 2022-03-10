package com.example.firebaseauthenticationapp.fragment

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.firebaseauthenticationapp.R
import com.example.firebaseauthenticationapp.databinding.FragmentUpdateEmailBinding
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class UpdateEmailFragment : Fragment() {
    private var _binding: FragmentUpdateEmailBinding? = null
    private val binding get() = _binding!!

    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateEmailBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(UpdateEmailFragmentDirections.actionEmailUpdated())
                }

            }
        )

        with(binding) {
            layoutPassword.visibility = View.VISIBLE
            layoutEmail.visibility = View.GONE

            btnAuth.setOnClickListener {
                val password = etPassword.text.toString().trim()

                if (password.isEmpty()) {
                    etPassword.error = "Harus Diisi"
                    etPassword.requestFocus()
                    return@setOnClickListener
                }

                user?.let {
                    val userCredentials =  EmailAuthProvider.getCredential(it.email!!, password)
                    it.reauthenticate(userCredentials).addOnCompleteListener {
                        if (it.isSuccessful) {
                            layoutPassword.visibility = View.GONE
                            layoutEmail.visibility = View.VISIBLE
                        } else if (it.exception is FirebaseAuthInvalidCredentialsException) {
                            etPassword.error = "Salah"
                            etPassword.requestFocus()
                        } else {
                            Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                btnUpdate.setOnClickListener { view ->
                    val email = etEmail.text.toString().trim()

                    if (email.isEmpty()) {
                        etEmail.error = "Email Harus Diisi"
                        etEmail.requestFocus()
                        return@setOnClickListener
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        etEmail.error = "Format Email Tidak Valid"
                        etEmail.requestFocus()
                        return@setOnClickListener
                    }

                    user?.let {
                        user.updateEmail(email).addOnCompleteListener {
                            if (it.isSuccessful) {
                                val actionEmailUpdated = UpdateEmailFragmentDirections.actionEmailUpdated()
                                Navigation.findNavController(view).navigate(actionEmailUpdated)
                            } else {
                                Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}