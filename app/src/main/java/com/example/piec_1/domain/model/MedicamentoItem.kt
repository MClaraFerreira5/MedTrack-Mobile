package com.example.piec_1.domain.model

import java.util.UUID

data class MedicationItem(
    val id: String = UUID.randomUUID().toString(),
    val nomeExibicao: String,
    val horario: String,
    val isContinuous: Boolean,
    val isGenerico: Boolean
)