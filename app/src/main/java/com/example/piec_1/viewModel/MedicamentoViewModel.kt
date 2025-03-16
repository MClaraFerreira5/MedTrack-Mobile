package com.example.piec_1.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.piec_1.database.AppDatabase
import com.example.piec_1.model.Medicamento
import kotlinx.coroutines.launch

class MedicamentoViewModel(application: Application) : AndroidViewModel(application) {

    private val medicamentoDao = AppDatabase.getDatabase(application).medicamentoDao()

    fun inserir(medicamento: Medicamento) {
        viewModelScope.launch {
            medicamentoDao.insertAll(listOf(medicamento))
        }
    }

    fun listarTodos(onResult: (List<Medicamento>) -> Unit) {
        viewModelScope.launch {
            val medicamentos = medicamentoDao.getMedicamentos() ?: emptyList()
            onResult(medicamentos)
        }
    }

}