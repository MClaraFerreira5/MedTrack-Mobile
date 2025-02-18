package com.example.piec_1.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piec_1.model.Medicamento
import com.example.piec_1.repository.MedicamentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicamentoViewModel @Inject constructor(
    private val medicamentoRepository: MedicamentoRepository
) : ViewModel() {

    fun inserir(medicamento: Medicamento) {
        viewModelScope.launch {
            medicamentoRepository.inserir(medicamento)
        }
    }

    fun listarTodos(onResult: (List<Medicamento>) -> Unit) {
        viewModelScope.launch {
            onResult(medicamentoRepository.listarTodos())
        }
    }

    fun listarNaoSincronizados(onResult: (List<Medicamento>) -> Unit) {
        viewModelScope.launch {
            onResult(medicamentoRepository.listarNaoSincronizados())
        }
    }

    fun sincronizar(id: Int) {
        viewModelScope.launch {
            medicamentoRepository.sincronizar(id)
        }
    }

    fun deletar(medicamento: Medicamento) {
        viewModelScope.launch {
            medicamentoRepository.deletar(medicamento)
        }
    }
}
