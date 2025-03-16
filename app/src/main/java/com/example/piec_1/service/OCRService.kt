package com.example.piec_1.service

import com.example.piec_1.model.Medicamento

class OCRService {

    fun extrairMedicamentoInfo(rawText: String) : Medicamento {
        val lines = rawText.lines().map {it.trim() }.filter { it.isNotEmpty() }

        var medicamentoNome: String? = null
        var compostoAtivo: String? = null
        var dosagem: String? = null

        var lastValidNome: String? = null

        for (line in lines) {

            if (line.contains(Regex("(?i)generic|genérico|gene"))) {
                medicamentoNome = "MEDICAMENTO GENÉRICO"
            }

            if (compostoAtivo == null && validarCompostoAtivo(line)) {
                compostoAtivo = line
                medicamentoNome = lastValidNome
                continue
            }

            lastValidNome = if (medicamentoNome == null) line else lastValidNome

            if (dosagem == null && validarDosagem(line)) {
                dosagem = line
            }

        }

        return Medicamento(
            id = System.currentTimeMillis(),
            nome = medicamentoNome ?: "Desconhecido",
            compostoAtivo = compostoAtivo ?: "Desconhecido",
            dosagem = dosagem ?: "Desconhecido",
            horarios = emptyList()
        )
    }

    private fun validarCompostoAtivo(text: String): Boolean {
        val sufixosCompostos = listOf("ol", "ato", "amida", "ila", "ina", "ona", "eto", "ana", "ida")
        val palavrasInvalidas = listOf("uso", "cápsula", "prescrição", "médica", "venda", "oral")

        return sufixosCompostos.any { text.lowercase().endsWith(it) } &&
                palavrasInvalidas.none { text.lowercase().contains(it) }
    }

    private fun validarDosagem(text: String): Boolean {
        val dosagemRegex = """(\d+(\.\d+)?\s?(mg|g|mcg|µg|mg/ml))""".toRegex()

        return dosagemRegex.find(text) != null

    }
}