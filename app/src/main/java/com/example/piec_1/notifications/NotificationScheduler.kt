package com.example.piec_1.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.piec_1.model.Medicamento
import java.time.LocalDate
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun agendarNotificacao(medicamento: Medicamento) {
        try {
            val horarios = medicamento.horarios
            val isContinuo = medicamento.usoContinuo

            if (isContinuo) {
                horarios.distinct().forEach { horario ->
                    scheduleDailyNotification(medicamento.id, medicamento.nome, horario)
                }
            } else {
                horarios.forEachIndexed { index, horario ->
                    val dataAgendamento = LocalDate.now()
                        .plusDays(index.toLong() / horarios.distinct().size)
                    scheduleSingleNotification(
                        medicamento.id,
                        medicamento.nome,
                        horario,
                        dataAgendamento.toString()
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("Notification", "Erro ao agendar: ${e.message}")
            scheduleUsingWorkManager(medicamento)
        }
    }

    fun scheduleUsingWorkManager(medicamento: Medicamento) {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        medicamento.horarios.forEach { horario ->
            val inputData = Data.Builder()
                .putLong("medicamentoId", medicamento.id)
                .putString("nome", medicamento.nome)
                .putString("horario", horario)
                .build()

            val delay = calculateDelay(horario) // Implemente esta função

            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }

    private fun calculateDelay(horario: String): Long {
        val (hours, minutes) = horario.split(":").take(2).map { it.toInt() }
        val now = Calendar.getInstance()
        val alarmTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
        }
        return alarmTime.timeInMillis - now.timeInMillis
    }

    private fun scheduleDailyNotification(medicamentoId: Long, nome: String, horario: String) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("medicamentoId", medicamentoId)
            putExtra("nome", nome)
            putExtra("horario", horario)
            putExtra("isContinuo", true)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            "${medicamentoId}_${horario}".hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (hour, minute) = horario.split(":").take(2).map { it.toInt() }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1) // Próximo dia se horário já passou
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun scheduleSingleNotification(
        medicamentoId: Long,
        nome: String,
        horario: String,
        dataAgendamento: String
    ) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("medicamentoId", medicamentoId)
            putExtra("nome", nome)
            putExtra("horario", horario)
            putExtra("isContinuo", false)
            putExtra("dataAgendamento", dataAgendamento)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            "${medicamentoId}_${horario}_${dataAgendamento}".hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (year, month, day) = dataAgendamento.split("-").map { it.toInt() }
        val (hour, minute) = horario.split(":").take(2).map { it.toInt() }

        val calendar = Calendar.getInstance().apply {
            set(year, month - 1, day, hour, minute, 0)
        }

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

}