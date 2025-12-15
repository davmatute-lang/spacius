package com.example.spacius

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.spacius.data.FirestoreRepository
import com.example.spacius.data.LugarFirestore
import com.example.spacius.data.ReservaFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var recyclerLugares: RecyclerView
    private lateinit var recyclerReservas: RecyclerView
    private lateinit var firestoreRepository: FirestoreRepository
    private val lugares = mutableListOf<LugarFirestore>()
    private val reservas = mutableListOf<ReservaFirestore>()
    private var mensajeView: TextView? = null

    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView - Iniciando")
        return try {
            val view = inflater.inflate(R.layout.fragment_home, container, false)

            recyclerLugares = view.findViewById(R.id.recyclerLugares)
            recyclerLugares.layoutManager = LinearLayoutManager(requireContext())

            recyclerReservas = view.findViewById(R.id.recyclerReservas)
            recyclerReservas.layoutManager = LinearLayoutManager(requireContext())

            firestoreRepository = FirestoreRepository()
            
            Log.d(TAG, "onCreateView - Completado exitosamente")
            view
        } catch (e: Exception) {
            Log.e(TAG, "Error en onCreateView: ${e.message}", e)
            null
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume - Cargando datos")
        cargarDatos()
    }

    private fun cargarDatos() {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Iniciando carga de datos...")
                
                // Inicializar lugares predefinidos si es la primera vez
                withContext(Dispatchers.IO) {
                    try {
                        firestoreRepository.inicializarLugaresPredefinidos()
                        Log.d(TAG, "Lugares predefinidos inicializados")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al inicializar lugares: ${e.message}", e)
                    }
                }
                
                // Cargar lugares y reservas
                val lugaresDisponibles = withContext(Dispatchers.IO) {
                    try {
                        firestoreRepository.obtenerLugaresDisponibles()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al obtener lugares: ${e.message}", e)
                        emptyList()
                    }
                }
                
                val reservasUsuario = withContext(Dispatchers.IO) {
                    try {
                        firestoreRepository.obtenerReservasUsuario()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al obtener reservas: ${e.message}", e)
                        emptyList()
                    }
                }

                // Actualizar UI en el hilo principal
                withContext(Dispatchers.Main) {
                    actualizarUILugares(lugaresDisponibles)
                    actualizarUIReservas(reservasUsuario)
                }
                
                Log.d(TAG, "Datos cargados: ${lugaresDisponibles.size} lugares, ${reservasUsuario.size} reservas")

            } catch (e: Exception) {
                Log.e(TAG, "Error general al cargar datos: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al cargar datos. Verifica tu conexión a internet.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun actualizarUILugares(lugaresDisponibles: List<LugarFirestore>) {
        try {
            lugares.clear()
            lugares.addAll(lugaresDisponibles)
            if (recyclerLugares.adapter == null) {
                recyclerLugares.adapter = LugarAdapter(
                    lugares,
                    onReservarClick = { lugar -> lanzarDetalleReserva(lugar) },
                    onFavoritoClick = { lugar -> toggleFavorito(lugar) }
                )
            } else {
                recyclerLugares.adapter?.notifyDataSetChanged()
            }
            mostrarEstadoLugares(lugaresDisponibles.isEmpty())
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar UI de lugares: ${e.message}", e)
        }
    }
    
    private fun actualizarUIReservas(reservasUsuario: List<ReservaFirestore>) {
        try {
            reservas.clear()
            reservas.addAll(reservasUsuario)
            if (recyclerReservas.adapter == null) {
                recyclerReservas.adapter = ReservaAdapter(reservas) { reserva ->
                    cancelarReserva(reserva)
                }
            } else {
                recyclerReservas.adapter?.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar UI de reservas: ${e.message}", e)
        }
    }

    private fun toggleFavorito(lugar: LugarFirestore) {
        val nuevoEstado = !lugar.esFavorito
        lugar.esFavorito = nuevoEstado

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val exito = firestoreRepository.actualizarFavorito(lugar.id, nuevoEstado)
                withContext(Dispatchers.Main) {
                    if (exito) {
                        val index = lugares.indexOfFirst { it.id == lugar.id }
                        if (index != -1) {
                            recyclerLugares.adapter?.notifyItemChanged(index)
                        }
                    } else {
                        lugar.esFavorito = !nuevoEstado
                        Toast.makeText(requireContext(), "Error al actualizar favorito", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al actualizar favorito: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    lugar.esFavorito = !nuevoEstado
                    Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cancelarReserva(reserva: ReservaFirestore) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val exito = firestoreRepository.cancelarReserva(reserva.id)
                withContext(Dispatchers.Main) {
                    if (exito) {
                        Toast.makeText(requireContext(), "Reserva cancelada", Toast.LENGTH_SHORT).show()
                        cargarDatos() // Recargar datos para actualizar las listas
                    } else {
                        Toast.makeText(requireContext(), "Error al cancelar la reserva", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al cancelar reserva: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error de conexión al cancelar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarEstadoLugares(sinLugares: Boolean) {
        try {
            if (sinLugares) {
                recyclerLugares.visibility = View.GONE

                if (mensajeView == null) {
                    mensajeView = TextView(requireContext()).apply {
                        text = "😔 No hay lugares disponibles en este momento\n\n" +
                                "Por favor, contacta al administrador o " +
                                "intenta nuevamente más tarde."
                        textSize = 16f
                        setTextColor(resources.getColor(android.R.color.black, null))
                        gravity = android.view.Gravity.CENTER
                        setPadding(32, 64, 32, 64)
                    }

                    (recyclerLugares.parent as? ViewGroup)?.addView(mensajeView)
                } else {
                    mensajeView?.visibility = View.VISIBLE
                }
            } else {
                recyclerLugares.visibility = View.VISIBLE
                mensajeView?.visibility = View.GONE
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al mostrar estado de lugares: ${e.message}", e)
        }
    }

    private fun lanzarDetalleReserva(lugar: LugarFirestore) {
        try {
            val fragment = ReservaFragment()
            fragment.arguments = Bundle().apply {
                putString("lugarId", lugar.id)
                putString("nombreLugar", lugar.nombre)
                putString("descripcion", lugar.descripcion)
                putString("fecha", lugar.fechaDisponible)
                putString("categoria", lugar.categoria)
                putString("imagenUrl", lugar.imagenUrl)
                putDouble("latitud", lugar.latitud)
                putDouble("longitud", lugar.longitud)
                putInt("capacidad", lugar.capacidadMaxima)
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        } catch (e: Exception) {
            Log.e(TAG, "Error al abrir detalle de reserva: ${e.message}", e)
            Toast.makeText(requireContext(), "Error al abrir detalles", Toast.LENGTH_SHORT).show()
        }
    }

    inner class ReservaAdapter(
        private val reservas: List<ReservaFirestore>,
        private val onCancelarClick: (ReservaFirestore) -> Unit
    ) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

        inner class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imgLugar: ImageView = itemView.findViewById(R.id.imgLugarReserva)
            val txtNombreLugar: TextView = itemView.findViewById(R.id.txtNombreLugarReserva)
            val txtFecha: TextView = itemView.findViewById(R.id.txtFechaReserva)
            val txtHora: TextView = itemView.findViewById(R.id.txtHoraReserva)
            val btnCancelar: Button = itemView.findViewById(R.id.btnCancelarReservaItem)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                ReservaViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reserva, parent, false)
            return ReservaViewHolder(view)
        }

        override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
            try {
                val reserva = reservas[position]
                holder.txtNombreLugar.text = reserva.lugarNombre
                holder.txtFecha.text = "Fecha: ${reserva.fecha}"
                holder.txtHora.text = "Hora: ${reserva.horaInicio} - ${reserva.horaFin}"
                holder.btnCancelar.setOnClickListener { onCancelarClick(reserva) }

                // Cargar imagen con manejo de errores mejorado
                if (reserva.imagenUrl.isNotEmpty()) {
                    Glide.with(holder.itemView.context)
                        .load(reserva.imagenUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .timeout(10000)
                        .into(holder.imgLugar)
                } else {
                    holder.imgLugar.setImageResource(R.drawable.ic_launcher_background)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en onBindViewHolder de ReservaAdapter: ${e.message}", e)
            }
        }

        override fun getItemCount() = reservas.size
    }

    inner class LugarAdapter(
        private val lugares: List<LugarFirestore>,
        private val onReservarClick: (LugarFirestore) -> Unit,
        private val onFavoritoClick: (LugarFirestore) -> Unit
    ) : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>() {

        inner class LugarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imgLugar: ImageView = itemView.findViewById(R.id.imgLugar)
            val txtNombre: TextView = itemView.findViewById(R.id.txtNombreLugar)
            val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcionLugar)
            val txtCapacidad: TextView = itemView.findViewById(R.id.txtCapacidad)
            val btnReservar: Button = itemView.findViewById(R.id.btnReservar)
            val btnFavorito: ImageButton = itemView.findViewById(R.id.btnFavorito)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LugarViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_lugar, parent, false))

        override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
            try {
                val lugar = lugares[position]
                holder.txtNombre.text = lugar.nombre
                holder.txtDescripcion.text = lugar.descripcion

                val capacidadInfo = if (lugar.capacidadMaxima > 0) {
                    "👥 Capacidad: ${lugar.capacidadMaxima} personas"
                } else {
                    "🏢 Espacio disponible"
                }

                val disponibilidadInfo = lugar.fechaDisponible.takeIf { it.isNotEmpty() } ?: "Disponible"
                holder.txtCapacidad.text = "$capacidadInfo • 📅 $disponibilidadInfo"

                // Cargar imagen con manejo de errores mejorado
                if (lugar.imagenUrl.isNotEmpty()) {
                    Glide.with(holder.itemView.context)
                        .load(lugar.imagenUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .timeout(10000)
                        .into(holder.imgLugar)
                } else {
                    holder.imgLugar.setImageResource(R.drawable.ic_launcher_background)
                }

                holder.btnReservar.setOnClickListener { onReservarClick(lugar) }
                holder.btnFavorito.setOnClickListener { onFavoritoClick(lugar) }

                // Actualizar icono de favorito
                if (lugar.esFavorito) {
                    holder.btnFavorito.setImageResource(R.drawable.ic_star_filled)
                } else {
                    holder.btnFavorito.setImageResource(R.drawable.ic_star_border)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en onBindViewHolder de LugarAdapter: ${e.message}", e)
            }
        }

        override fun getItemCount() = lugares.size
    }
}
