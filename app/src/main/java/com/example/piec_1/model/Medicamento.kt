package com.example.piec_1.model

data class Medicamento(

    val nome: String,
    val compostoAtivo: String,
    val dosagem: String,
    val dataRegistro: Long,
    val sincronizado: Boolean = false
)
