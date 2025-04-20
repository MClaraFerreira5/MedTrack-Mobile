package com.example.piec_1.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.piec_1.MainActivity
import com.example.piec_1.R
import com.example.piec_1.notifications.NotificationHelper.formatarHorario

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicamentoId = intent.getLongExtra("medicamentoId", -1)
        var nome = intent.getStringExtra("nome") ?: return
        val horario = formatarHorario(intent.getStringExtra("horario").toString())
        val compostoAtivo = intent.getStringExtra("compostoAtivo") ?: return

        if (nome == "MEDICAMENTO GENÉRICO") {
            nome = compostoAtivo
        }

        val deepLinkIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("app://telaCamera/$medicamentoId/$horario")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            medicamentoId.toInt(),
            deepLinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText("São $horario, está na hora de tomar $nome")
            .setBigContentTitle("Hora do remédio!")
            .setSummaryText("MedTrack - Lembrete")

        NotificationCompat.Builder(context, "medicamento_channel")
            .setContentTitle("Hora de tomar $nome")
            .setContentText("Horário: $horario")
            .setSmallIcon(R.drawable.medtrack_white_icon)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.medtrack_white_icon))
            .setStyle(bigTextStyle)
            .setColorized(true)
            .setColor(ContextCompat.getColor(context, R.color.notification_color))
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