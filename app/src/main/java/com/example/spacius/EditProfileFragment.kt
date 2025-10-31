package com.example.spacius

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.spacius.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { imageUri ->
                uploadImageToFirebase(imageUri)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        loadUserProfile()

        binding.ivProfileImage.setOnClickListener {
            openGallery()
        }

        binding.btnSaveChanges.setOnClickListener {
            saveUserChanges()
        }

        binding.btnChangePassword.setOnClickListener {
            sendPasswordResetEmail()
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                Glide.with(this).load(photoUrl).placeholder(R.drawable.ic_user).into(binding.ivProfileImage)
            } else {
                binding.ivProfileImage.setImageResource(R.drawable.ic_user)
            }
            binding.etProfileName.setText(user.displayName)
            binding.etProfileEmail.setText(user.email)
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        binding.etProfilePhone.setText(document.getString("telefono"))
                        binding.etProfileCedula.setText(document.getString("cedula"))
                    }
                }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val user = auth.currentUser ?: return
        val storageRef = storage.reference.child("profile_images/${user.uid}.jpg")
        Toast.makeText(context, "Actualizando foto...", Toast.LENGTH_SHORT).show()
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val profileUpdates = UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build()
                    user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "¡Foto de perfil actualizada!", Toast.LENGTH_SHORT).show()
                            Glide.with(this).load(downloadUri).into(binding.ivProfileImage)
                        } else {
                            Toast.makeText(context, "Error al actualizar el perfil.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener { e ->
                // --- INICIO DE LA MODIFICACIÓN 1 ---
                // Se cambió Toast.LONG por Toast.LENGTH_LONG
                Toast.makeText(context, "Error al subir la imagen: ${e.message}", Toast.LENGTH_LONG).show()
                // --- FIN DE LA MODIFICACIÓN 1 ---
            }
    }

    private fun saveUserChanges() {
        val user = auth.currentUser ?: return

        val newName = binding.etProfileName.text.toString().trim()
        val newPhone = binding.etProfilePhone.text.toString().trim()
        val newCedula = binding.etProfileCedula.text.toString().trim()

        if (newName.isEmpty()) {
            Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(newName).build()
        user.updateProfile(profileUpdates)

        val userUpdates = mapOf(
            "nombre" to newName,
            "telefono" to newPhone,
            "cedula" to newCedula
        )

        firestore.collection("users").document(user.uid).update(userUpdates)
            .addOnSuccessListener {
                Toast.makeText(context, "¡Perfil actualizado exitosamente!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al actualizar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendPasswordResetEmail() {
        val user = auth.currentUser
        if (user?.email != null) {
            auth.sendPasswordResetEmail(user.email!!)
                .addOnSuccessListener {
                    // --- INICIO DE LA MODIFICACIÓN 2 ---
                    // Se cambió Toast.LONG por Toast.LENGTH_LONG
                    Toast.makeText(context, "Se ha enviado un correo para cambiar tu contraseña", Toast.LENGTH_LONG).show()
                    // --- FIN DE LA MODIFICACIÓN 2 ---
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "No se pudo encontrar el correo del usuario", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
