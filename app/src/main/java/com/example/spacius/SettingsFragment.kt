package com.example.spacius

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rowEditarPerfil = view.findViewById<LinearLayout>(R.id.rowEditarPerfil)
        val rowEspacios = view.findViewById<LinearLayout>(R.id.rowEspacios)
        val rowHistorial = view.findViewById<LinearLayout>(R.id.rowHistorial)
        val rowCerrarSesion = view.findViewById<LinearLayout>(R.id.rowCerrarSesion)

        // Editar Perfil → Próximamente
        rowEditarPerfil.setOnClickListener {
            Toast.makeText(requireContext(), "Editar Perfil - Próximamente", Toast.LENGTH_SHORT).show()
        }

        // Espacios → Próximamente
        rowEspacios.setOnClickListener {
            Toast.makeText(requireContext(), "Espacios - Próximamente", Toast.LENGTH_SHORT).show()
        }

        // Historial → Próximamente
        rowHistorial.setOnClickListener {
            Toast.makeText(requireContext(), "Historial - Próximamente", Toast.LENGTH_SHORT).show()
        }

        // Cerrar sesión → abrir LoginActivity y cerrar MainActivity
        rowCerrarSesion.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()
        }
    }
}
