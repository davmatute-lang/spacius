package com.example.spacius.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NotificationService {

    private val db = FirebaseFirestore.getInstance()
    private val userId: String? get() = FirebaseAuth.getInstance().currentUser?.uid

    fun getNotificationsFlow(): Flow<List<Notification>> = callbackFlow {
        val listener = userId?.let {
            db.collection("users").document(it).collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        close(e)
                        return@addSnapshotListener
                    }
                    val notifications = snapshot?.toObjects(Notification::class.java) ?: emptyList()
                    trySend(notifications)
                }
        }
        awaitClose { listener?.remove() }
    }
}