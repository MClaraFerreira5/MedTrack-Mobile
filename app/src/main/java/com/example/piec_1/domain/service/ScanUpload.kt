package com.example.piec_1.domain.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.piec_1.MainActivity
import com.example.piec_1.data.PreferencesManager
import com.example.piec_1.data.remote.MedicamentoData
import com.example.piec_1.data.repository.ScanRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ScanUpload(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "ScanUpload"
    }

    private val token = PreferencesManager.getToken(appContext)
    private val repository = ScanRepository(appContext)

    override suspend fun doWork(): Result {
        Log.d(TAG, "🚀 Worker iniciado!")

        if (token == null) {
            Log.e(TAG, "❌ Token não encontrado!")
            return Result.failure()
        }

        val pendingScans = repository.getPendingScans()
        Log.d(TAG, "📦 Scans pendentes: ${pendingScans.size}")

        if (pendingScans.isEmpty()) {
            return Result.success()
        }

        var allSuccess = true

        pendingScans.forEach { scan ->
            try {
                val file = File(Uri.parse(scan.imagePath).path ?: "")
                Log.d(TAG, "📸 Arquivo: ${file.absolutePath}, existe: ${file.exists()}, tamanho: ${file.length()} bytes")

                if (file.exists()) {
                    val response = enviarImagemParaApi(file)

                    if (response != null) {
                        Log.d(TAG, "✅ Upload bem sucedido! Medicamento: ${response.nome}")
                        repository.updateStatus(scan.id, "CONCLUIDO")

                        enviarNotificacaoComDados(response)
                        file.delete()
                    } else {
                        Log.e(TAG, "❌ Falha no upload")
                        allSuccess = false
                    }
                } else {
                    Log.e(TAG, "❌ Arquivo não encontrado")
                    allSuccess = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exceção: ${e.message}", e)
                allSuccess = false
            }
        }

        return if (allSuccess) Result.success() else Result.retry()
    }

    private suspend fun enviarImagemParaApi(file: File): MedicamentoData? {
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

        var body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        var response = repository.apiService.scanMedicamento("Bearer $token", body)

        if (response.isSuccessful) {
            return response.body()?.data
        }

        Log.e(TAG, "Tentativa com 'file' falhou: ${response.code()}")

        body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        response = repository.apiService.scanMedicamento("Bearer $token", body)

        if (response.isSuccessful) {
            return response.body()?.data
        }

        Log.e(TAG, "Tentativa com 'image' falhou: ${response.code()}")

        body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        response = repository.apiService.scanMedicamento("Bearer $token", body)

        if (response.isSuccessful) {
            return response.body()?.data
        }

        Log.e(TAG, "Tentativa com 'photo' falhou: ${response.code()}")

        try {
            val imageBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        } catch (e: Exception) {
            Log.e(TAG, "Tentativa raw falhou: ${e.message}")
        }

        return null
    }

    private fun enviarNotificacaoComDados(medicamentoData: MedicamentoData) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "offline_scan_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Scans Offline",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

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
            .setContentTitle("💊 Medicamento Processado")
            .setContentText(medicamentoData.nome ?: "Clique para confirmar as informações")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("""
                    💊 ${medicamentoData.nome ?: "Medicamento"}
                    📋 ${medicamentoData.agente_ativo ?: ""}
                    💊 Dosagem: ${medicamentoData.dosagem ?: "N/A"}
                    📦 Quantidade: ${medicamentoData.quantidade ?: "N/A"}
                    📅 Validade: ${medicamentoData.validade ?: "N/A"}
                    
                    ✨ Clique para confirmar ou editar as informações
                """.trimIndent())
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}