package com.example.spacius

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val rowHistorial: LinearLayout = view.findViewById(R.id.rowHistorial)
        val rowCerrarSesion: LinearLayout = view.findViewById(R.id.rowCerrarSesion)
        val btnGuardar: Button = view.findViewById(R.id.btnGuardar)
        val btnCambiarFoto: Button = view.findViewById(R.id.btnCambiarFoto)

        btnCambiarFoto.setOnClickListener {
            Toast.makeText(requireContext(), "Función de cambiar foto próximamente", Toast.LENGTH_SHORT).show()
        }

        btnGuardar.setOnClickListener {
            Toast.makeText(requireContext(), "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
        }

        rowHistorial.setOnClickListener {
            Toast.makeText(requireContext(), "Ir al historial (pendiente)", Toast.LENGTH_SHORT).show()
        }

        rowCerrarSesion.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}
