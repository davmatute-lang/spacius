package com.example.spacius

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.spacius.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var isLoading = false

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

        setupUI()
        loadUserProfile()
    }

    private fun setupUI() {
        // Click en la imagen para cambiarla
        binding.ivProfileImage.setOnClickListener {
            if (!isLoading) openGallery()
        }

        // Botón guardar cambios
        binding.btnSaveChanges.setOnClickListener {
            if (!isLoading) saveUserChanges()
        }

        // Botón cambiar contraseña
        binding.btnChangePassword.setOnClickListener {
            if (!isLoading) showPasswordResetDialog()
        }
    }

    private fun loadUserProfile() {
        setLoading(true)
        val user = auth.currentUser
        
        if (user != null) {
            // Cargar foto de perfil
            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivProfileImage)
            } else {
                binding.ivProfileImage.setImageResource(R.drawable.ic_user)
            }
            
            // Cargar datos básicos
            binding.etProfileName.setText(user.displayName ?: "")
            binding.etProfileEmail.setText(user.email ?: "")
            
            // Cargar datos adicionales de Firestore
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        binding.etProfilePhone.setText(document.getString("telefono") ?: "")
                        binding.etProfileCedula.setText(document.getString("cedula") ?: "")
                    }
                    setLoading(false)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                    setLoading(false)
                }
        } else {
            setLoading(false)
            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val user = auth.currentUser ?: return
        
        setLoading(true)
        
        try {
            // Comprimir la imagen antes de subirla
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, imageUri))
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            }
            
            val compressedImage = compressImage(bitmap)
            
            val storageRef = storage.reference.child("profile_images/${user.uid}.jpg")
            
            storageRef.putBytes(compressedImage)
                .addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                    // Opcional: mostrar progreso en UI
                }
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        updateProfilePhoto(downloadUri)
                    }
                }
                .addOnFailureListener { e ->
                    setLoading(false)
                    Toast.makeText(context, "Error al subir la imagen: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            setLoading(false)
            Toast.makeText(context, "Error al procesar la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun compressImage(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        var quality = 90
        
        // Comprimir hasta que sea menor a 500KB
        do {
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 10
        } while (outputStream.size() > 500 * 1024 && quality > 20)
        
        return outputStream.toByteArray()
    }
    
    private fun updateProfilePhoto(photoUri: Uri) {
        val user = auth.currentUser ?: return
        
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(photoUri)
            .build()
            
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    Toast.makeText(context, "¡Foto de perfil actualizada!", Toast.LENGTH_SHORT).show()
                    Glide.with(this)
                        .load(photoUri)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(binding.ivProfileImage)
                } else {
                    Toast.makeText(context, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserChanges() {
        val user = auth.currentUser ?: return

        val newName = binding.etProfileName.text.toString().trim()
        val newPhone = binding.etProfilePhone.text.toString().trim()
        val newCedula = binding.etProfileCedula.text.toString().trim()

        // Validaciones
        if (newName.isEmpty()) {
            binding.etProfileName.error = "El nombre no puede estar vacío"
            binding.etProfileName.requestFocus()
            return
        }
        
        if (newName.length < 3) {
            binding.etProfileName.error = "El nombre debe tener al menos 3 caracteres"
            binding.etProfileName.requestFocus()
            return
        }

        if (newPhone.isNotEmpty() && !isValidPhone(newPhone)) {
            binding.etProfilePhone.error = "Número de teléfono inválido"
            binding.etProfilePhone.requestFocus()
            return
        }

        if (newCedula.isNotEmpty() && !isValidCedula(newCedula)) {
            binding.etProfileCedula.error = "Cédula inválida (debe tener 10 dígitos)"
            binding.etProfileCedula.requestFocus()
            return
        }

        setLoading(true)

        // Actualizar displayName en Firebase Auth
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()
            
        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                // Actualizar datos en Firestore
                updateFirestoreData(newName, newPhone, newCedula)
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(context, "Error al actualizar nombre: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun updateFirestoreData(name: String, phone: String, cedula: String) {
        val user = auth.currentUser ?: return
        
        val userUpdates = hashMapOf<String, Any>(
            "nombre" to name,
            "telefono" to phone,
            "cedula" to cedula
        )

        firestore.collection("users").document(user.uid)
            .update(userUpdates)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(context, "¡Perfil actualizado exitosamente!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Si el documento no existe, crearlo
                firestore.collection("users").document(user.uid)
                    .set(userUpdates)
                    .addOnSuccessListener {
                        setLoading(false)
                        Toast.makeText(context, "¡Perfil actualizado exitosamente!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { error ->
                        setLoading(false)
                        Toast.makeText(context, "Error al actualizar datos: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            }
    }
    
    private fun isValidPhone(phone: String): Boolean {
        // Validar que sea un número de 10 dígitos (Ecuador)
        return phone.matches(Regex("^[0-9]{10}$"))
    }
    
    private fun isValidCedula(cedula: String): Boolean {
        // Validar que tenga 10 dígitos
        return cedula.matches(Regex("^[0-9]{10}$"))
    }
    
    private fun showPasswordResetDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cambiar Contraseña")
            .setMessage("Se enviará un correo a ${auth.currentUser?.email} con instrucciones para cambiar tu contraseña. ¿Deseas continuar?")
            .setPositiveButton("Enviar") { _, _ ->
                sendPasswordResetEmail()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun sendPasswordResetEmail() {
        val user = auth.currentUser
        val email = user?.email
        
        if (email.isNullOrEmpty()) {
            Toast.makeText(context, "No se pudo encontrar el correo del usuario", Toast.LENGTH_SHORT).show()
            return
        }
        
        setLoading(true)
        
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(
                    context, 
                    "Se ha enviado un correo a $email para cambiar tu contraseña", 
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun setLoading(loading: Boolean) {
        isLoading = loading
        binding.progressBar?.isVisible = loading
        binding.btnSaveChanges.isEnabled = !loading
        binding.btnChangePassword.isEnabled = !loading
        binding.ivProfileImage.isClickable = !loading
        
        // Cambiar texto del botón durante loading
        if (loading) {
            binding.btnSaveChanges.text = "Guardando..."
        } else {
            binding.btnSaveChanges.text = "Guardar Cambios"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
