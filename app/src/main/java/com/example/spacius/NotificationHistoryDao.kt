package com.example.spacius

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationHistoryDao {

    @Insert
    suspend fun insert(notification: NotificationHistoryItem)

    @Query("SELECT * FROM notification_history ORDER BY timestamp DESC")
    fun getAllNotifications(): LiveData<List<NotificationHistoryItem>>
}
