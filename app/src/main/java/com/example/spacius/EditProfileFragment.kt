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

    companion object {
        private const val COLLECTION_USERS = "users"
        private const val STORAGE_PROFILE_IMAGES = "profile_images"
        private const val MAX_IMAGE_SIZE_KB = 500
        private const val INITIAL_COMPRESSION_QUALITY = 90
        private const val MIN_COMPRESSION_QUALITY = 20
        private const val PHONE_CEDULA_LENGTH = 10
        private const val MIN_NAME_LENGTH = 3
    }

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
        
        if (user == null) {
            setLoading(false)
            showToast("Usuario no autenticado")
            return
        }
        
        // Cargar foto de perfil
        loadProfileImage(user.photoUrl)
        
        // Cargar datos básicos
        binding.etProfileName.setText(user.displayName.orEmpty())
        binding.etProfileEmail.setText(user.email.orEmpty())
        
        // Cargar datos adicionales de Firestore
        firestore.collection(COLLECTION_USERS)
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.etProfilePhone.setText(document.getString("telefono").orEmpty())
                    binding.etProfileCedula.setText(document.getString("cedula").orEmpty())
                }
                setLoading(false)
            }
            .addOnFailureListener { e ->
                showToast("Error al cargar datos: ${e.message}")
                setLoading(false)
            }
    }
    
    private fun loadProfileImage(photoUrl: Uri?) {
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
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val user = auth.currentUser ?: run {
            showToast("Usuario no autenticado")
            return
        }
        
        setLoading(true)
        
        try {
            val bitmap = loadBitmapFromUri(imageUri)
            val compressedImage = compressImage(bitmap)
            val storageRef = storage.reference.child("$STORAGE_PROFILE_IMAGES/${user.uid}.jpg")
            
            storageRef.putBytes(compressedImage)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        updateProfilePhoto(downloadUri)
                    }.addOnFailureListener { e ->
                        handleError("Error al obtener URL: ${e.message}")
                    }
                }
                .addOnFailureListener { e ->
                    handleError("Error al subir la imagen: ${e.message}")
                }
        } catch (e: Exception) {
            handleError("Error al procesar la imagen: ${e.message}")
        }
    }
    
    private fun loadBitmapFromUri(imageUri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, imageUri))
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
        }
    }
    
    private fun compressImage(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        var quality = INITIAL_COMPRESSION_QUALITY
        val maxSize = MAX_IMAGE_SIZE_KB * 1024
        
        // Comprimir hasta que sea menor al tamaño máximo
        do {
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 10
        } while (outputStream.size() > maxSize && quality > MIN_COMPRESSION_QUALITY)
        
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
                    showToast("¡Foto de perfil actualizada!")
                    // Recargar imagen sin caché
                    Glide.with(this)
                        .load(photoUri)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(binding.ivProfileImage)
                } else {
                    showToast("Error al actualizar el perfil")
                }
            }
    }

    private fun saveUserChanges() {
        val user = auth.currentUser ?: run {
            showToast("Usuario no autenticado")
            return
        }

        val newName = binding.etProfileName.text.toString().trim()
        val newPhone = binding.etProfilePhone.text.toString().trim()
        val newCedula = binding.etProfileCedula.text.toString().trim()

        // Validaciones
        if (!validateInputs(newName, newPhone, newCedula)) {
            return
        }

        setLoading(true)

        // Actualizar displayName en Firebase Auth
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()
            
        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                updateFirestoreData(user.uid, newName, newPhone, newCedula)
            }
            .addOnFailureListener { e ->
                handleError("Error al actualizar nombre: ${e.message}")
            }
    }
    
    private fun validateInputs(name: String, phone: String, cedula: String): Boolean {
        return when {
            name.isEmpty() -> {
                binding.etProfileName.error = "El nombre no puede estar vacío"
                binding.etProfileName.requestFocus()
                false
            }
            name.length < MIN_NAME_LENGTH -> {
                binding.etProfileName.error = "El nombre debe tener al menos $MIN_NAME_LENGTH caracteres"
                binding.etProfileName.requestFocus()
                false
            }
            phone.isNotEmpty() && !isValidPhone(phone) -> {
                binding.etProfilePhone.error = "Número de teléfono inválido (10 dígitos)"
                binding.etProfilePhone.requestFocus()
                false
            }
            cedula.isNotEmpty() && !isValidCedula(cedula) -> {
                binding.etProfileCedula.error = "Cédula inválida (10 dígitos)"
                binding.etProfileCedula.requestFocus()
                false
            }
            else -> true
        }
    }
    
    private fun updateFirestoreData(uid: String, name: String, phone: String, cedula: String) {
        val userUpdates = hashMapOf<String, Any>(
            "nombre" to name,
            "telefono" to phone,
            "cedula" to cedula
        )

        firestore.collection(COLLECTION_USERS)
            .document(uid)
            .set(userUpdates, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                setLoading(false)
                showToast("¡Perfil actualizado exitosamente!")
            }
            .addOnFailureListener { error ->
                handleError("Error al actualizar datos: ${error.message}")
            }
    }
    
    private fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^[0-9]{$PHONE_CEDULA_LENGTH}$"))
    }
    
    private fun isValidCedula(cedula: String): Boolean {
        return cedula.matches(Regex("^[0-9]{$PHONE_CEDULA_LENGTH}$"))
    }
    
    private fun showPasswordResetDialog() {
        val email = auth.currentUser?.email
        
        AlertDialog.Builder(requireContext())
            .setTitle("Cambiar Contraseña")
            .setMessage("Se enviará un correo a $email con instrucciones para cambiar tu contraseña. ¿Deseas continuar?")
            .setPositiveButton("Enviar") { _, _ ->
                sendPasswordResetEmail()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun sendPasswordResetEmail() {
        val email = auth.currentUser?.email
        
        if (email.isNullOrEmpty()) {
            showToast("No se pudo encontrar el correo del usuario")
            return
        }
        
        setLoading(true)
        
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                setLoading(false)
                showToast("Se ha enviado un correo a $email para cambiar tu contraseña", isLong = true)
            }
            .addOnFailureListener { e ->
                handleError("Error: ${e.message}")
            }
    }
    
    // Métodos auxiliares para manejo de UI
    private fun showToast(message: String, isLong: Boolean = false) {
        Toast.makeText(
            context, 
            message, 
            if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        ).show()
    }
    
    private fun handleError(message: String) {
        setLoading(false)
        showToast(message)
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
