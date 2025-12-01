package com.example.spacius

import android.util.Log
import com.example.spacius.utils.NotificationUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationHistoryDao by lazy { AppDatabase.getDatabase(this).notificationHistoryDao() }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM messages here.
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Message Notification Body: ${notification.body}")

            val title = notification.title ?: "Notificación"
            val body = notification.body ?: "Has recibido una nueva notificación."

            // 1. Show the notification in the status bar
            NotificationUtils.showSimpleNotification(this, title, body)

            // 2. Save the notification to the database for history
            val notificationData = NotificationHistoryItem(title = title, message = body)
            // Using coroutines to insert into the database
            CoroutineScope(Dispatchers.IO).launch {
                notificationHistoryDao.insert(notificationData)
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
