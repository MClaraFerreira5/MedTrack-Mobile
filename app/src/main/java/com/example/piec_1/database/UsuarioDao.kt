package com.example.piec_1.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.piec_1.model.Usuario

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(usuario: Usuario)

    @Query("SELECT * FROM usuario LIMIT 1")
    suspend fun getUsuario(): Usuario
}