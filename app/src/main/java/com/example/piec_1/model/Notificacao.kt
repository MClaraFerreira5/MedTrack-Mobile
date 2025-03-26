package com.example.piec_1.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "notificacoes",
    foreignKeys = [ForeignKey(
        entity = Medicamento::class,
        parentColumns = ["id"],
        childColumns = ["medicamentoId"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class Notificacao(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicamentoId: Long,
    val horario: String,
    val dataAgendamento: String,
    val exibida: Boolean = false
)
