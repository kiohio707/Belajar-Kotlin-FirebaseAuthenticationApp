package com.example.firebaseauthenticationapp.fragment

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.firebaseauthenticationapp.R
import com.example.firebaseauthenticationapp.databinding.FragmentChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.ktx.Firebase

class ChangePasswordFragment : Fragment() {
    private var _binding : FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        with(binding) {
            layoutPassword.visibility = View.VISIBLE
            layoutNewPassword.visibility = View.GONE

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
                            layoutNewPassword.visibility = View.VISIBLE
                        } else if (it.exception is FirebaseAuthInvalidCredentialsException) {
                            etPassword.error = "Salah"
                            etPassword.requestFocus()
                        } else {
                            Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                btnUpdate.setOnClickListener { view ->
                    val newPassword = etNewPassword.text.toString().trim()
                    val newPasswordConfirm = etNewPasswordConfirm.text.toString().trim()

                    if (newPassword.isEmpty() || password.length < 6)  {
                        etPassword.error = "Password Harus Lebih Dari 6 Karakter"
                        etPassword.requestFocus()
                        return@setOnClickListener
                    }

                    if (newPassword != newPasswordConfirm) {
                        etNewPasswordConfirm.error = "Password Tidak Sama"
                        etNewPasswordConfirm.requestFocus()
                        return@setOnClickListener
                    }

                    user?.let {
                        user.updatePassword(newPassword).addOnCompleteListener {
                            if (it.isSuccessful) {
                                val actionPasswordChanged = ChangePasswordFragmentDirections.actionPasswordChanged()
                                Navigation.findNavController(view).navigate(actionPasswordChanged)
                                Toast.makeText(activity, "Password Berhasil Diganti", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }

}