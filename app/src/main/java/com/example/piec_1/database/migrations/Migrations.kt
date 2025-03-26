package com.example.piec_1.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1,2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE medicamentos ADD COLUMN usoContinuo INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_2_3 = object : Migration(2,3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE notificacoes(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                medicamentoId INTEGER NOT NULL,
                horario TEXT NOT NULL,
                dataAgendamento TEXT NOT NULL,
                exibida INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(medicamentoId) REFERENCES medicamentos(id)
            )
        """.trimIndent())
    }
}