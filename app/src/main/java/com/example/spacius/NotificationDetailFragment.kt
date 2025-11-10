package com.example.spacius

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationDetailFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification_detail, container, false)

        recyclerView = view.findViewById(R.id.rv_notifications)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val notifications = listOf(
            Notification("Reserva Confirmada", "Tu reserva para la sala de reuniones ha sido confirmada.", "hace 2 minutos"),
            Notification("Recordatorio de Reserva", "Tu reserva para la sala de yoga es mañana a las 10 AM.", "hace 1 hora"),
            Notification("Nuevo Espacio Disponible", "Se ha añadido una nueva sala de meditación.", "hace 5 horas"),
            Notification("Reserva Cancelada", "Tu reserva para la sala de conferencias ha sido cancelada.", "hace 1 día")
        )

        notificationAdapter = NotificationAdapter(notifications)
        recyclerView.adapter = notificationAdapter

        return view
    }
}
