package com.example.piec_1.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.piec_1.model.Confirmacao

@Dao
interface ConfirmacaoDao {
    @Insert
    suspend fun insert(confirmacao: Confirmacao)

    @Query("SELECT * FROM confirmacoes WHERE medicamentoId = :medicamentoId AND data = :data AND horario = :horario")
    suspend fun getConfirmacao(medicamentoId: Long, data: String, horario: String): Confirmacao?

    @Query("SELECT * FROM confirmacoes WHERE sincronizado = 0")
    suspend fun getConfirmacoesNaoSincronizadas(): List<Confirmacao>

    @Update
    suspend fun update(confirmacao: Confirmacao)
}