package com.example.spacius

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.spacius.utils.NotificationUtils

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val lugarNombre = inputData.getString("lugarNombre") ?: return Result.failure()
        val horaInicio = inputData.getString("horaInicio") ?: return Result.failure()

        NotificationUtils.showBookingReminderNotification(
            applicationContext,
            lugarNombre,
            horaInicio
        )

        return Result.success()
    }
}
