package com.example.piec_1.domain.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.piec_1.MainActivity
import com.example.piec_1.data.remote.MedicamentoData
import com.example.piec_1.data.repository.MedTrackRepository
import com.example.piec_1.utils.exceptions.TokenNaoEncontradoException
import com.google.gson.Gson
import java.io.File

class ScanUpload(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "ScanUpload"
        private const val STATUS_CONCLUIDO = "CONCLUIDO"
    }

    private val repository = MedTrackRepository(appContext)

    override suspend fun doWork(): Result {
        val pendingScans = repository.getPendingScans()

        if (pendingScans.isEmpty()) {
            return Result.success()
        }

        var allSuccess = true

        pendingScans.forEach { scan ->
            try {
                val file = File(scan.imagePath.toUri().path.orEmpty())

                if (!file.exists()) {
                    Log.e(TAG, "Arquivo nao encontrado: ${scan.imagePath}")
                    allSuccess = false
                    return@forEach
                }

                val medicamentoData = repository.uploadScanPendente(file)

                if (medicamentoData != null) {
                    repository.updateScanStatus(scan.id, STATUS_CONCLUIDO)
                    enviarNotificacaoComDados(medicamentoData)
                    file.delete()
                } else {
                    allSuccess = false
                }
            } catch (_: TokenNaoEncontradoException) {
                return Result.failure()
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao processar scan pendente: ${e.message}", e)
                allSuccess = false
            }
        }

        return if (allSuccess) Result.success() else Result.retry()
    }

    private fun enviarNotificacaoComDados(medicamentoData: MedicamentoData) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "offline_scan_channel"

        val channel = NotificationChannel(
            channelId,
            "Scans Offline",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val medicamentoJson = Gson().toJson(medicamentoData)
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            action = "OPEN_CONFIRMATION"
            putExtra("medicamento_json", medicamentoJson)
            putExtra("navigate_to_confirmation", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_upload_done)
            .setContentTitle("Medicamento Processado")
            .setContentText(medicamentoData.nome ?: "Clique para confirmar as informacoes")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        """
                        ${medicamentoData.nome ?: "Medicamento"}
                        ${medicamentoData.agente_ativo ?: ""}
                        Dosagem: ${medicamentoData.dosagem ?: "N/A"}
                        Quantidade: ${medicamentoData.quantidade ?: "N/A"}
                        Validade: ${medicamentoData.validade ?: "N/A"}
                        
                        Clique para confirmar ou editar as informacoes
                        """.trimIndent()
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
