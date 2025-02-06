package com.example.piec_1.repository

import com.example.piec_1.database.MedicamentoDao
import com.example.piec_1.model.Medicamento

class MedicamentoRepository(private val medicamentoDao: MedicamentoDao) {

    suspend fun inserir(medicamento: Medicamento) {
        medicamentoDao.insert(medicamento)
    }

    suspend fun listarTodos(): List<Medicamento> {
        return medicamentoDao.listarMedicamentos()
    }

    suspend fun listarNaoSincronizados(): List<Medicamento> {
        return medicamentoDao.getMedicamentosNaoSincronizados()
    }

    suspend fun sincronizar(id: Int) {
        medicamentoDao.sincronizarMedicamento(id)
    }

    suspend fun deletar(medicamento: Medicamento) {
        medicamentoDao.deletar(medicamento)
    }
}
