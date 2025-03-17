package com.example.piec_1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicamentos")
data class Medicamento(

    @PrimaryKey val id: Long,
    val nome: String,
    val compostoAtivo: String,
    val dosagem: String,
    val horarios: List<String>,
    val usoContinuo: Boolean,
    val sincronizado: Boolean = false
)
