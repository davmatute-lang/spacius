package com.example.spacius

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.spacius.data.FirestoreRepository
import com.example.spacius.data.LugarFirestore
import kotlinx.coroutines.launch

/**
 * Fragment para mostrar la lista de espacios guardados como favoritos por el usuario.
 */
class FavoriteSpacesFragment : Fragment(R.layout.fragment_favorite_spaces) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyMessageTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var firestoreRepository: FirestoreRepository
    private lateinit var favoriteAdapter: FavoriteLugarAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewFavorites)
        emptyMessageTextView = view.findViewById(R.id.textViewEmptyFavorites)
        progressBar = view.findViewById(R.id.progressBarFavorites)
        swipeRefresh = view.findViewById(R.id.swipeRefreshFavorites)
        firestoreRepository = FirestoreRepository()

        setupRecyclerView()
        setupSwipeRefresh()
        loadFavoriteSpaces()
    }
    
    private fun setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            loadFavoriteSpaces()
        }
        swipeRefresh.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteLugarAdapter(
            lugares = emptyList(),
            onItemClick = { lugar -> 
                // Navegar a detalles del lugar
                Toast.makeText(context, "Ver detalles de ${lugar.nombre}", Toast.LENGTH_SHORT).show()
            },
            onRemoveFavorite = { lugar ->
                removeFavorite(lugar)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = favoriteAdapter
    }

    private fun loadFavoriteSpaces() {
        progressBar.isVisible = true
        swipeRefresh.isRefreshing = false
        
        lifecycleScope.launch {
            try {
                val favoritePlaces = firestoreRepository.obtenerLugaresFavoritos()
                
                if (favoritePlaces.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyMessageTextView.visibility = View.VISIBLE
                } else {
                    favoriteAdapter.updateData(favoritePlaces)
                    recyclerView.visibility = View.VISIBLE
                    emptyMessageTextView.visibility = View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al cargar favoritos: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.isVisible = false
            }
        }
    }
    
    private fun removeFavorite(lugar: LugarFirestore) {
        lifecycleScope.launch {
            try {
                val success = firestoreRepository.actualizarFavorito(lugar.id, false)
                if (success) {
                    Toast.makeText(context, "${lugar.nombre} eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    loadFavoriteSpaces() // Recargar la lista
                } else {
                    Toast.makeText(context, "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Adapter mejorado para mostrar los lugares favoritos con diseño personalizado.
     */
    inner class FavoriteLugarAdapter(
        private var lugares: List<LugarFirestore>,
        private val onItemClick: (LugarFirestore) -> Unit,
        private val onRemoveFavorite: (LugarFirestore) -> Unit
    ) : RecyclerView.Adapter<FavoriteLugarAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cardView: CardView = itemView.findViewById(R.id.cardViewFavorite)
            val imageView: ImageView = itemView.findViewById(R.id.ivFavoritePlace)
            val nombre: TextView = itemView.findViewById(R.id.tvFavoriteName)
            val descripcion: TextView = itemView.findViewById(R.id.tvFavoriteDescription)
            val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemoveFavorite)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favorite_place, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val lugar = lugares[position]
            
            holder.nombre.text = lugar.nombre
            holder.descripcion.text = lugar.descripcion.ifEmpty { "Sin descripción" }
            
            // Cargar imagen con Glide
            if (lugar.imagenUrl.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(lugar.imagenUrl)
                    .placeholder(R.drawable.ic_spaces)
                    .error(R.drawable.ic_spaces)
                    .centerCrop()
                    .into(holder.imageView)
            } else {
                holder.imageView.setImageResource(R.drawable.ic_spaces)
            }
            
            // Click en el card completo
            holder.cardView.setOnClickListener {
                onItemClick(lugar)
            }
            
            // Click en el botón de eliminar
            holder.btnRemove.setOnClickListener {
                onRemoveFavorite(lugar)
            }
        }

        override fun getItemCount() = lugares.size

        fun updateData(newLugares: List<LugarFirestore>) {
            this.lugares = newLugares
            notifyDataSetChanged()
        }
    }
}