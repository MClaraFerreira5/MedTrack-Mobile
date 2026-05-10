package com.example.piec_1.domain.model

data class MedicamentoDomain(
    val id: Long,
    val nome: String,
    val compostoAtivo: String,
    val dosagem: String,
    val frequenciaUso: FrequenciaUsoDomain
)