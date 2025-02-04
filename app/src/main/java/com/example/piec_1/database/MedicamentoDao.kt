package com.example.piec_1.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.piec_1.model.Medicamento

@Dao
interface MedicamentoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicamento: Medicamento)

    @Query("SELECT * FROM medicamentos ORDER BY dataRegistro DESC")
    suspend fun listarMedicamentos(): List<Medicamento>

    @Query("SELECT * FROM medicamentos WHERE sincronizado = 0")
    suspend fun getMedicamentosNaoSincronizados(): List<Medicamento>

    @Query("UPDATE medicamentos SET sincronizado = 1 WHERE id = :id")
    suspend fun sincronizarMedicamento(id: Int)

    @Delete
    suspend fun deletar(medicamento: Medicamento)
}