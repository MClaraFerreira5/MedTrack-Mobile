package com.example.piec_1.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.piec_1.MainActivity
import com.example.piec_1.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicamentoId = intent.getLongExtra("medicamentoId", -1)
        val nome = intent.getStringExtra("nome") ?: return
        val horario = intent.getStringExtra("horario") ?: return

        // Cria o deep link para o Compose
        val deepLinkIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("app://telaCamera/$medicamentoId/$horario")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Mostra a notificação em tela cheia
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            medicamentoId.toInt(),
            deepLinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        NotificationCompat.Builder(context, "medicamento_channel")
            .setContentTitle("Hora de tomar $nome")
            .setContentText("Horário: ${horario.format("HH:mm")}")
            .setSmallIcon(R.drawable.medtrack_white_icon)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .build()
            .let { notification ->
                context.getSystemService(NotificationManager::class.java).notify(
                    medicamentoId.toInt(),
                    notification
                )
            }
    }
}