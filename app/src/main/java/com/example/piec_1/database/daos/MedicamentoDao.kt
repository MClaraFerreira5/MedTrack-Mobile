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

    @Query("""
        SELECT * FROM medicamentos 
        WHERE nome LIKE '%' || :nome || '%' 
        OR compostoAtivo LIKE '%' || :compostoAtivo || '%'
        LIMIT 1
    """)
    suspend fun getMedicamentoPorNomeOuComposto(nome: String, compostoAtivo: String): Medicamento?

    @Query("""
        SELECT * FROM medicamentos
        WHERE LOWER(nome) LIKE LOWER(:nome) 
        OR LOWER(compostoAtivo) LIKE LOWER(:compostoAtivo)
        OR LOWER(nome) LIKE '%' || LOWER(:termoBusca) || '%'
        OR LOWER(compostoAtivo) LIKE '%' || LOWER(:termoBusca) || '%'
        LIMIT 1
    """)
    suspend fun buscarMedicamentoFlexivel(
        nome: String,
        compostoAtivo: String,
        termoBusca: String = nome
    ): Medicamento?

}