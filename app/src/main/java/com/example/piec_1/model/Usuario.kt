package com.example.piec_1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey val id: Long,
    val nome: String,
    val email: String,
    val nomeUsuario: String
)
