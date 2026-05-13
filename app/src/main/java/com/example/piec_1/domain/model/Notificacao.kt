package com.example.piec_1.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.piec_1.data.local.entity.MedicamentoEntity

@Entity(
    tableName = "notificacoes",
    foreignKeys = [ForeignKey(
        entity = MedicamentoEntity::class,
        parentColumns = ["id"],
        childColumns = ["medicamentoId"],
        onDelete = ForeignKey.NO_ACTION
    )],
    indices = [Index(value = ["medicamentoId"])]
)
data class Notificacao(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicamentoId: Long,
    val horario: String,
    val dataAgendamento: String,
    val exibida: Boolean = false
)
