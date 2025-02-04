package com.example.piec_1.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.piec_1.database.AppDatabase
import com.example.piec_1.model.Medicamento
import com.example.piec_1.repository.MedicamentoRepository
import kotlinx.coroutines.launch

class MedicamentoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = MedicamentoRepository(db.medicamentoDao())

    fun inserir(medicamento: Medicamento) {
        viewModelScope.launch {
            repository.inserir(medicamento)
        }
    }

    fun listarTodos(onResult: (List<Medicamento>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.listarTodos())
        }
    }

    fun listarNaoSincronizados(onResult: (List<Medicamento>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.listarNaoSincronizados())
        }
    }

    fun sincronizar(id: Int) {
        viewModelScope.launch {
            repository.sincronizar(id)
        }
    }

    fun deletar(medicamento: Medicamento) {
        viewModelScope.launch {
            repository.deletar(medicamento)
        }
    }
}
