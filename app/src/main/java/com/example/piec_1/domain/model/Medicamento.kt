package com.example.piec_1.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.xml.validation.Validator

@Entity(tableName = "medicamentos")
data class Medicamento(

    @PrimaryKey val id: Long,
    val nome: String,
    val compostoAtivo: String,
    val dosagem: String,
    val horarios: List<String>,
    val usoContinuo: Boolean,
    val quantidade: String,
    val validade: String?,
    val sincronizado: Boolean = false
)
