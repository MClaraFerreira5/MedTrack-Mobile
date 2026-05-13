package com.example.piec_1.ui.navigation

import com.example.piec_1.domain.model.MedicamentoCapturadoDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object NavigationManager {
    private val _shouldNavigate = MutableStateFlow<MedicamentoCapturadoDomain?>(null)
    val shouldNavigate = _shouldNavigate.asStateFlow()

    fun setMedicamento(medicamento: MedicamentoCapturadoDomain) {
        _shouldNavigate.value = medicamento
    }

    fun reset() {
        _shouldNavigate.value = null
    }
}
