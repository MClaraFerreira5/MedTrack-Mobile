package com.example.piec_1.model

data class DadosConfirmacaoRequest(
    val usuarioId: Long,
    val medicamentoId: Long,
    val horario: String,
    val data: String,
    val foiTomado: Boolean,
    val observacao:String?
)
