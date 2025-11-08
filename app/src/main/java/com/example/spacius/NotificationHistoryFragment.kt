package com.example.spacius

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationHistoryAdapter
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification_history, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_notifications)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NotificationHistoryAdapter(emptyList())
        recyclerView.adapter = adapter

        db = AppDatabase.getDatabase(requireContext())

        db.notificationHistoryDao().getAllNotifications().observe(viewLifecycleOwner, Observer {
            adapter.updateData(it)
        })

        return view
    }
}
