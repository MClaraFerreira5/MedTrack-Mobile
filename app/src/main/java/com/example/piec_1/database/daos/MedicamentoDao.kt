package com.example.piec_1.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.piec_1.model.Medicamento

@Dao
interface MedicamentoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(medicamentos: List<Medicamento>)

    @Query("SELECT * FROM medicamentos")
    suspend fun getMedicamentos(): List<Medicamento>
}