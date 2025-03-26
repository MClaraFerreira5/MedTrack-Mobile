package com.example.piec_1.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val medicamentoId = inputData.getLong("medicamentoId", -1)
        val nome = inputData.getString("nome") ?: return Result.failure()
        val horario = inputData.getString("horario") ?: return Result.failure()

        NotificationHelper.showNotification(applicationContext, medicamentoId, nome, horario)

        return Result.success()
    }
}