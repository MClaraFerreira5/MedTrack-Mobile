package com.example.piec_1.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.piec_1.data.local.entity.MedicamentoEntity

@Entity(
    tableName = "confirmacoes",
    foreignKeys = [ForeignKey(
        entity = MedicamentoEntity::class,
        parentColumns = ["id"],
        childColumns = ["medicamentoId"],
        onDelete = ForeignKey.NO_ACTION
    )],
    indices = [Index(value = ["medicamentoId"])]
)
data class Confirmacao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicamentoId: Long,
    val horario: String,
    val data: String,
    val foiTomado: Boolean,
    val observacao: String? = null,
    val sincronizado: Boolean = false
)
