package com.example.spacius.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spacius.R
import com.example.spacius.data.*
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private val lugares = mutableListOf<Lugar>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerLugares)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        db = AppDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            if (db.lugarDao().countLugares() == 0) {
                db.lugarDao().insertAll(getLugaresPredefinidos())
            }

            lugares.addAll(db.lugarDao().getAllLugares())
            recyclerView.adapter = LugarAdapter(lugares) { lugar ->
                lanzarDetalleReserva(lugar)
            }
        }

        return view
    }

    private fun lanzarDetalleReserva(lugar: Lugar) {
        val fragment = ReservaFragment()
        fragment.arguments = Bundle().apply {
            putInt("idLugar", lugar.id)          // <- Nuevo: ID del lugar
            putString("nombreLugar", lugar.nombre)
            putString("descripcion", lugar.descripcion)
            putString("fecha", lugar.fechaDisponible)
            putString("hora", lugar.horaDisponible)
            putString("imagenUrl", lugar.imagenUrl)
            putDouble("latitud", lugar.latitud)   // <- Esto es nuevo
            putDouble("longitud", lugar.longitud) // <- Esto es nuevo
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


    // Adapter para RecyclerView
    inner class LugarAdapter(
        private val lugares: List<Lugar>,
        private val onReservarClick: (Lugar) -> Unit
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
            holder.txtCapacidad.text = "Disponibilidad: ${lugar.fechaDisponible} - ${lugar.horaDisponible}"

            Glide.with(holder.itemView.context)
                .load(lugar.imagenUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgLugar)

            holder.btnReservar.setOnClickListener { onReservarClick(lugar) }
        }

        override fun getItemCount() = lugares.size
    }

    // Tus 10 lugares exactos (copiados tal cual)
    private fun getLugaresPredefinidos(): List<Lugar> = listOf(
        Lugar(1, "Canchas del Parque Samanes", "En el Parque Samanes encontrarás más de 50 canchas modernas y seguras, diseñadas para que vivas la pasión del deporte al máximo.", -2.1022530106411046, -79.90182885626803, "https://lh3.googleusercontent.com/gps-cs-s/AC9h4nosDGYYszXIdBn6JphVwQQbuImqAon1ifoNx0JEhYMNoabRi98C6cTtfgRZFHmlsRCARN_kfbymZ-ps8tEit1haxfixMPLJpkOOIUqjeYahlW-G0ISjSxrci2HPVSzEps6op_0P=w408-h544-k-no", "Lunes a Domingo", "10h00 a 18h00"),
        Lugar(2, "Canchas de HandBall", "En el Parque Samanes encontrarás modernas canchas de handball diseñadas para que vivas la emoción de este deporte dinámico y lleno de adrenalina.", -2.1030848752860667, -79.9082475832117, "https://lh3.googleusercontent.com/gps-cs-s/AC9h4noLdTbGA5x4kLuFbVylIp0Xd2VIS-UK9zcE0O9JY89PKkQkHOfQ_spnHN-DDw8JNqpYh4aoA9ORkXq1V1_S-Bpv44T1w-_clI2uew4hscgQ-NrZYB3ERJ0qA5bLaaOQtGSuuo1iVA=w425-h240-k-no", "Lunes a Domingo", "10h00 a 18h00"),
        Lugar(3, "Área de Picnic Parque Samanes", "El área de picnic del Parque Samanes es el rincón ideal para escapar de la rutina y disfrutar al aire libre.", -2.105220191117523, -79.90329556145007, "https://lh3.googleusercontent.com/gps-cs-s/AC9h4nrqjHXzMCHOf74sqpNq9ir-rhobLV9fBSXxXlIdaZV9dYVX-jjmgUx9yaCLVLL38Vm3GZ-hAU_Q6TIZbUY8Sze25lTvLcAFxW_M0EUSa1cWRSkAG525JkdPeUkXr_tFXXwm0_p4=w408-h306-k-no", "Lunes a Domingo", "10h00 a 18h00"),
        Lugar(4, "Canchas de Vóley Playero", "En el Parque Samanes tienes la oportunidad de sentirte en la playa sin salir de la ciudad gracias a sus modernas canchas de vóley playero.", -2.1014850476965927, -79.89866715429523, "https://lh3.googleusercontent.com/gps-cs-s/AC9h4noE7va4JdGf2zQmeTXtokNPeKQ7FW2lkC8zSyWPjec9wlcb22t0SJyQa3UIkZqYINFxAkYKvvTxsZwKYUw9Wk6DKIw0CUVZjq18W6BsH8dJwgot8s6hlpPrrNjKbAJ22kxHNfc-DA=w426-h240-k-no", "Lunes a Domingo", "10h00 a 18h00"),
        Lugar(5, "Parque de la Octava Alborada", "En el corazón de la Alborada Octava Etapa se encuentra este parque lleno de vida, perfecto para compartir con la familia y amigos.", -2.137780953469543, -79.90288114274225, "https://lh3.googleusercontent.com/gps-cs-s/AC9h4nrV67Hero0IbxysevA2Y-LcP6V0TeHDIXB5Rwt3v0sXbTSerXLVOlxN6BnaaIpWdjeVRRrihnJV9OVNVFYC6Pmw4ESZs0bgxQBOAtxtjIZLA9KN8rngOa-aEw94JHT7WMIshekS3g=w425-h240-k-no", "Jueves a Domingo", "12h00 a 16h00"),
        Lugar(6, "Parque Acuático Bastión Popular", "En el norte de Guayaquil, el Parque Acuático Bastión Popular es el lugar ideal para refrescarse y divertirse en familia.", -2.090274535360757, -79.93658413155697, "https://lh3.googleusercontent.com/gps-cs-s/AC9h4notjwG3qSwr2OLOEH0-pC0fmEn1CjPMjIpf-8rEVsCaFKAFdAaO6bOmc4EcNB-2hGTzYMjHwfSRfvnwn9Pf-clP5IyIFsZmLylU3fdRv5XVCdJa5tEmwkvBO_tiqIWFU6_2FZWJ=w408-h271-k-no", "Miércoles a Sábado", "10h00 a 18h00"),
        Lugar(7, "Parque Acuático Juan Montalvo", "Ubicado en el sector norte de Guayaquil, el Parque Acuático Juan Montalvo es el lugar perfecto para pasar un día lleno de diversión.", -2.1226221947933346, -79.92231074271157, "https://lh3.googleusercontent.com/gps-cs-s/AC9h4noJOi1UDlOpot23ysVmq_Bm8WhjVD50EUzGFNRxmyzmlYUztgaznnac-IQIjlSYPRR4LbgLCai1cp71F5Xszk3UMI6GYc3YDCOP0urVSp7TT12i9jkzd4aJO7mX0DksGZT_LMlP=w408-h306-k-no", "Miércoles a Sábado", "11h00 a 18h00"),
        Lugar(8, "Parque Acuático Metropolitano", "Situado en una de las zonas verdes más amplias de Guayaquil, el Parque Acuático Metropolitano ofrece diversión para toda la familia.", -2.079310380171689, -79.9683355984489, "https://lh3.googleusercontent.com/gps-cs-s/AC9h4nqT2be37YZCXQwn0THAhkCyhJSzPN7-QtNKPa2a9h-UFsFsawf55Icn-zveFxv3v_ReT0BcWAc3Uk_lqsCOWFjZnkR70nOFsY8yLdQe_JJC74d9Ztrs6ETwOAkQpt4KnupPXpe1=w408-h306-k-no", "Martes a Domingo", "9h00 a 17h00"),
        Lugar(9, "Boda Colectiva Estadio Capwell", "Celebra un momento único y especial en un lugar emblemático de Guayaquil: el Estadio Capwell.", -2.20646486125551, -79.89380421608391, "https://lh3.googleusercontent.com/gps-cs-s/AC9h4nrSHxF9Nr1mpQjjXNAAYmo1IWwrjAKvStEJfX8r_YrQfBs6CqyqQkcNHrN0g5ffFefdemaJKRr6jKuHW-c_yhEEI95CZu_WwkhJP53z9iZCqgSdmd8_P7nrfOlC05ukijTb6g=w408-h268-k-no", "Solo Sábados", "14h00 a 17h00"),
        Lugar(10, "Boda Colectiva Estadio Monumental", "Vive un momento único en un lugar emblemático de Guayaquil: el Estadio Monumental abre sus puertas para bodas colectivas.", -2.185785658404195, -79.92524802490722, "https://lh3.googleusercontent.com/gps-cs-s/AC9h4noxctYUeQffpBdPb7OGi5xhpPD5iOTeQGk5UPRxyYmC7sobcu1DtyYfiZJ1EUfbnLSj79BHSALIEvRcpzTtsxNGu4iHN8H_lS5x35Jy-S3YC6BzRSW2ycW3eOa_fTbLybVXtI8hJQ=w408-h317-k-no", "Solo Sábado", "14h00 a 16h00")
    )
}


