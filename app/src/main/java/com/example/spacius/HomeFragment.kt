package com.example.spacius.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spacius.R
import com.example.spacius.ui.ReservaFragment
import com.example.spacius.data.*
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var firestoreRepository: FirestoreRepository
    private val lugares = mutableListOf<LugarFirestore>()
    private var mensajeView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerLugares)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        firestoreRepository = FirestoreRepository()

        cargarLugaresDisponibles()
        
        return view
    }
    
    override fun onResume() {
        super.onResume()
        cargarLugaresDisponibles() // Recargar lugares disponibles
    }
    
    /**
     * Cargar lugares disponibles desde Firestore
     */
    private fun cargarLugaresDisponibles() {
        lifecycleScope.launch {
            try {
                android.util.Log.d("HomeFragment", "=== INICIANDO CARGA DE LUGARES ===")
                
                // Inicializar lugares predefinidos si es necesario
                val inicializado = firestoreRepository.inicializarLugaresPredefinidos()
                android.util.Log.d("HomeFragment", "Lugares inicializados: $inicializado")
                
                // Obtener todos los lugares primero
                val todosLugares = firestoreRepository.obtenerLugares()
                android.util.Log.d("HomeFragment", "Total lugares encontrados: ${todosLugares.size}")
                
                if (todosLugares.isNotEmpty()) {
                    android.util.Log.d("HomeFragment", "Ejemplo de lugar: ${todosLugares.first()}")
                }
                
                // Obtener lugares disponibles (no reservados por el usuario)
                val lugaresDisponibles = firestoreRepository.obtenerLugaresDisponibles()
                android.util.Log.d("HomeFragment", "Lugares disponibles: ${lugaresDisponibles.size}")
                
                // Si no hay lugares disponibles pero sí lugares totales, mostrar detalle
                if (lugaresDisponibles.isEmpty() && todosLugares.isNotEmpty()) {
                    val reservasUsuario = firestoreRepository.obtenerLugaresReservados()
                    android.util.Log.d("HomeFragment", "Usuario tiene ${reservasUsuario.size} reservas activas")
                    reservasUsuario.forEach { reserva ->
                        android.util.Log.d("HomeFragment", "Reserva: LugarID=${reserva.lugarId}, Estado=${reserva.estado}")
                    }
                }
                
                // Actualizar la lista
                lugares.clear()
                lugares.addAll(lugaresDisponibles)
                
                // Configurar adapter
                if (recyclerView.adapter == null) {
                    recyclerView.adapter = LugarAdapter(lugares) { lugar ->
                        lanzarDetalleReserva(lugar)
                    }
                } else {
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                
                // Mostrar estado de lugares
                mostrarEstadoLugares(lugaresDisponibles.isEmpty())
                
                android.util.Log.d("HomeFragment", "=== CARGA COMPLETADA ===")
                
            } catch (e: Exception) {
                android.util.Log.e("HomeFragment", "Error al cargar lugares: ${e.message}", e)
                Toast.makeText(requireContext(), "Error al cargar lugares: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Mostrar mensaje cuando no hay lugares disponibles
     */
    private fun mostrarEstadoLugares(sinLugares: Boolean) {
        if (sinLugares) {
            recyclerView.visibility = View.GONE
            
            if (mensajeView == null) {
                mensajeView = TextView(requireContext()).apply {
                    text = "🎉 ¡Todos los lugares están reservados!\n\n" +
                           "Revisa el calendario para ver tus reservas o " +
                           "espera a que se liberen espacios."
                    textSize = 16f
                    setTextColor(resources.getColor(android.R.color.black, null))
                    gravity = android.view.Gravity.CENTER
                    setPadding(32, 64, 32, 64)
                }
                
                (recyclerView.parent as? ViewGroup)?.addView(mensajeView)
            } else {
                mensajeView?.visibility = View.VISIBLE
            }
        } else {
            recyclerView.visibility = View.VISIBLE
            mensajeView?.visibility = View.GONE
        }
    }

    /**
     * Lanzar fragmento de detalle/reserva
     */
    private fun lanzarDetalleReserva(lugar: LugarFirestore) {
        val fragment = ReservaFragment()
        fragment.arguments = Bundle().apply {
            putString("lugarId", lugar.id)
            putString("nombreLugar", lugar.nombre)
            putString("descripcion", lugar.descripcion)
            putString("fecha", lugar.fechaDisponible)
            putString("hora", lugar.horaDisponible)
            putString("imagenUrl", lugar.imagenUrl)
            putDouble("latitud", lugar.latitud)
            putDouble("longitud", lugar.longitud)
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * Adapter para RecyclerView con datos de Firestore
     */
    inner class LugarAdapter(
        private val lugares: List<LugarFirestore>,
        private val onReservarClick: (LugarFirestore) -> Unit
    ) : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>() {

        inner class LugarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imgLugar: ImageView = itemView.findViewById(R.id.imgLugar)
            val txtNombre: TextView = itemView.findViewById(R.id.txtNombreLugar)
            val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcionLugar)
            val txtCapacidad: TextView = itemView.findViewById(R.id.txtCapacidad)
            val btnReservar: Button = itemView.findViewById(R.id.btnReservar)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LugarViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_lugar, parent, false))

        override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
            val lugar = lugares[position]
            holder.txtNombre.text = lugar.nombre
            holder.txtDescripcion.text = lugar.descripcion
            holder.txtCapacidad.text = "📅 ${lugar.fechaDisponible} | 🕐 ${lugar.horaDisponible}"

            Glide.with(holder.itemView.context)
                .load(lugar.imagenUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgLugar)

            holder.btnReservar.setOnClickListener { onReservarClick(lugar) }
        }

        override fun getItemCount() = lugares.size
    }
}