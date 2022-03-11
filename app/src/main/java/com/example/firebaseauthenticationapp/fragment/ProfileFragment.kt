package com.example.firebaseauthenticationapp.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.firebaseauthenticationapp.activity.HomeActivity
import com.example.firebaseauthenticationapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageUri: Uri
    private lateinit var auth: FirebaseAuth

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser

        with(binding) {
            if (user != null) {
                if (user.photoUrl != null) {
                    Picasso.get().load(user.photoUrl).into(ivProfile)
                } else {
                    Picasso.get().load("https://picsum.photos/200").into(ivProfile)
                }

                etName.setText(user.displayName)
                etMail.setText(user.email)

                if (user.isEmailVerified) {
                    ivVerify.visibility = View.VISIBLE
                } else {
                    ivUnverify.visibility = View.VISIBLE
                }
            }
        }

        binding.ivProfile.setOnClickListener {
            intentCamera()
        }

        binding.btnUpdate.setOnClickListener {
            val image = when {
                ::imageUri.isInitialized -> imageUri
                user?.photoUrl == null -> Uri.parse("https://picsum.photos/200")
                else -> user.photoUrl
            }

            val name = binding.etName.text.toString()

            if (name.isEmpty()) {
                binding.etName.error = "Nama Harus Diisi"
                binding.etName.requestFocus()
                return@setOnClickListener
            }

            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(image)
                .build().also {
                    user?.updateProfile(it)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(activity, it.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
        }

        with(binding) {
            ivUnverify.setOnClickListener {
                user?.sendEmailVerification()?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            activity,
                            "Email Verifikasi telah Dikirim",
                            Toast.LENGTH_SHORT
                        ).show()
                        Intent(requireContext(), HomeActivity::class.java).also {
                            //It's for no turning back after back button pressed
                            it.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                        }
                    } else {
                        Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        with(binding) {
            etMail.setOnClickListener {
                val actionUpdateEmail = ProfileFragmentDirections.actionUpdateEmail()
                Navigation.findNavController(it).navigate(actionUpdateEmail)
            }

        }

        with(binding) {
            tvChangePassword.setOnClickListener {
                val actionChangePassword = ProfileFragmentDirections.actionChangePassword()
                Navigation.findNavController(it).navigate(actionChangePassword)


            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun intentCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            activity?.packageManager?.let {
                intent.resolveActivity(it).also {
                    startActivityForResult(intent, REQUEST_CAMERA_PERMISSION)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CAMERA_PERMISSION && resultCode == RESULT_OK) {
            val imgBitmap = data?.extras?.get("data") as Bitmap
            uploadImage(imgBitmap)
        } else {
            Toast.makeText(context, "Error onActivityResult : $requestCode", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun uploadImage(imgBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val ref =
            FirebaseStorage.getInstance().reference.child("img/${FirebaseAuth.getInstance().currentUser?.uid}")

        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val image = baos.toByteArray()

        ref.putBytes(image).addOnCompleteListener {
            if (it.isSuccessful) {
                ref.downloadUrl.addOnCompleteListener {
                    it.result?.let {
                        imageUri = it
                        binding.ivProfile.setImageBitmap(imgBitmap)
                    }
                }
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


}
