package com.example.piec_1.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.piec_1.model.Notificacao

@Dao
interface NotificacaoDao {
    @Insert
    suspend fun insert(notificacao: Notificacao)

    @Query("SELECT * FROM notificacoes WHERE medicamentoId = :medicamentoId AND exibida = 0")
    suspend fun getNotificacoesPendentes(medicamentoId: Long): List<Notificacao>

    @Update
    suspend fun marcarComoExibida(notificacao: Notificacao)
}