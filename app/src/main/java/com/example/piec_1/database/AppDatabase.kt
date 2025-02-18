package com.example.piec_1.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.piec_1.model.Medicamento
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Database(entities = [Medicamento::class], version = 1, exportSchema = false)
@InstallIn(SingletonComponent::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicamentoDao(): MedicamentoDao
}

