package com.example.piec_1.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.piec_1.MainActivity
import com.example.piec_1.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter


object NotificationHelper {

    fun showNotification(context: Context, medicamentoId: Long, nome: String, horario: String) {
        createNotificationChannel(context)

        val horarioFormatado = formatarHorario(horario)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("medicamentoId", medicamentoId)
            putExtra("horario", horarioFormatado)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            medicamentoId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText("Não esqueça de tomar seu medicamento $nome no horário: $horarioFormatado")
            .setBigContentTitle("Hora do remédio!")
            .setSummaryText("MedTrack - Lembrete")

        val notification = NotificationCompat.Builder(context, "medicamento_channel")
            .setContentTitle("Hora do remédio: $nome")
            .setContentText("Horário: $horario")
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.medtrack_white_icon))
            .setStyle(bigTextStyle)
            .setColorized(true)
            .setColor(ContextCompat.getColor(context, R.color.notification_color))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(medicamentoId.toInt(), notification)
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "medicamento_channel",
                "Lembretes de Medicamentos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações para lembrar de tomar medicamentos"
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                vibrationPattern = longArrayOf(0, 200, 100, 200)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun formatarHorario(horario: String): String {
        return try {
            LocalTime.parse(horario).format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            Log.w("FormatarHorario", "Formato inválido: $horario")
            if (horario.length >= 5) horario.substring(0, 5) else "--:--"
        }
    }
}