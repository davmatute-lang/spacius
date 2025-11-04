package com.example.spacius

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.spacius.data.LugarFirestore
import com.example.spacius.data.FirestoreRepository
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var firestoreRepository: FirestoreRepository
    private val lugares = mutableListOf<LugarFirestore>()
    private var mensajeView: TextView? = null

    private lateinit var imageCarousel: ViewPager2
    private val imageList = listOf(
        R.drawable.carr1,
        R.drawable.carr2,
        R.drawable.carr3,
        R.drawable.carr4
    )
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var autoScrollRunnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerLugares)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        imageCarousel = view.findViewById(R.id.imageCarousel)
        setupCarousel()

        firestoreRepository = FirestoreRepository()
        cargarLugaresDisponibles()

        return view
    }

    private fun setupCarousel() {
        imageCarousel.adapter = ImageCarouselAdapter(imageList)
        autoScrollRunnable = Runnable {
            var currentItem = imageCarousel.currentItem
            currentItem = (currentItem + 1) % imageList.size
            imageCarousel.setCurrentItem(currentItem, true)
            handler.postDelayed(autoScrollRunnable, 6000) // 6 segundos
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(autoScrollRunnable, 6000)
        cargarLugaresDisponibles()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(autoScrollRunnable)
    }

    private fun cargarLugaresDisponibles() {
        lifecycleScope.launch {
            try {
                val lugaresDisponibles = firestoreRepository.obtenerLugaresDisponibles()
                lugares.clear()
                lugares.addAll(lugaresDisponibles)

                if (recyclerView.adapter == null) {
                    recyclerView.adapter = LugarAdapter(
                        lugares,
                        onReservarClick = { lugar -> lanzarDetalleReserva(lugar) },
                        onFavoritoClick = { lugar -> toggleFavorito(lugar) }
                    )
                } else {
                    recyclerView.adapter?.notifyDataSetChanged()
                }

                mostrarEstadoLugares(lugaresDisponibles.isEmpty())

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar lugares: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun toggleFavorito(lugar: LugarFirestore) {
        val nuevoEstado = !lugar.esFavorito
        lugar.esFavorito = nuevoEstado

        lifecycleScope.launch {
            val exito = firestoreRepository.actualizarFavorito(lugar.id, nuevoEstado)
            if (exito) {
                val index = lugares.indexOfFirst { it.id == lugar.id }
                if (index != -1) {
                    recyclerView.adapter?.notifyItemChanged(index)
                }
            } else {
                lugar.esFavorito = !nuevoEstado
            }
        }
    }

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

    private fun lanzarDetalleReserva(lugar: LugarFirestore) {
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
            val lugar = lugares[position]
            holder.txtNombre.text = lugar.nombre
            holder.txtDescripcion.text = lugar.descripcion

            val capacidadInfo = if (lugar.capacidadMaxima > 0) {
                " Capacidad: ${lugar.capacidadMaxima} personas"
            } else {
                "🏢 Espacio disponible"
            }

            val disponibilidadInfo = lugar.fechaDisponible.takeIf { it.isNotEmpty() } ?: "Disponible"
            holder.txtCapacidad.text = "$capacidadInfo • 📅 $disponibilidadInfo"

            Glide.with(holder.itemView.context)
                .load(lugar.imagenUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgLugar)

            holder.btnReservar.setOnClickListener { onReservarClick(lugar) }

            holder.btnFavorito.setOnClickListener { onFavoritoClick(lugar) }

            if (lugar.esFavorito) {
                holder.btnFavorito.setImageResource(R.drawable.ic_star_filled)
            } else {
                holder.btnFavorito.setImageResource(R.drawable.ic_star_border)
            }
        }

        override fun getItemCount() = lugares.size
    }
}
