package com.example.piec_1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicamentos")
data class Medicamento(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val compostoAtivo: String,
    val dosagem: String,
    val dataRegistro: Long,
    val sincronizado: Boolean = false
)
