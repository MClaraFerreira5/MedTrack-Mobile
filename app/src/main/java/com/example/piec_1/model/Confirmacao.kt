package com.example.piec_1.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "confirmacoes",
    foreignKeys = [ForeignKey(
        entity = Medicamento::class,
        parentColumns = ["id"],
        childColumns = ["medicamentoId"],
        onDelete = ForeignKey.NO_ACTION
    )]
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
