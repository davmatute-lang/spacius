package com.example.spacius

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spacius.data.FirestoreRepository
import com.example.spacius.data.LugarFirestore
import kotlinx.coroutines.launch

/**
 * Fragment para mostrar la lista de espacios guardados como favoritos por el usuario.
 */
class FavoriteSpacesFragment : Fragment(R.layout.fragment_favorite_spaces) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyMessageTextView: TextView
    private lateinit var firestoreRepository: FirestoreRepository
    private lateinit var favoriteAdapter: FavoriteLugarAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewFavorites)
        emptyMessageTextView = view.findViewById(R.id.textViewEmptyFavorites)
        firestoreRepository = FirestoreRepository()

        setupRecyclerView()
        loadFavoriteSpaces()
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteLugarAdapter(emptyList()) // Inicialmente vacío
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = favoriteAdapter
    }

    private fun loadFavoriteSpaces() {
        lifecycleScope.launch {
            val favoritePlaces = firestoreRepository.obtenerLugaresFavoritos()
            if (favoritePlaces.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyMessageTextView.visibility = View.VISIBLE
            } else {
                favoriteAdapter.updateData(favoritePlaces)
                recyclerView.visibility = View.VISIBLE
                emptyMessageTextView.visibility = View.GONE
            }
        }
    }

    /**
     * Adapter para mostrar los lugares favoritos en una lista simple.
     */
    inner class FavoriteLugarAdapter(private var lugares: List<LugarFirestore>) :
        RecyclerView.Adapter<FavoriteLugarAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nombre: TextView = itemView.findViewById(android.R.id.text1)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.nombre.text = lugares[position].nombre
        }

        override fun getItemCount() = lugares.size

        fun updateData(newLugares: List<LugarFirestore>) {
            this.lugares = newLugares
            notifyDataSetChanged()
        }
    }
}