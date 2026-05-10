package com.example.piec_1.data.local

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

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `confirmacoes` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `medicamentoId` INTEGER NOT NULL,
                `horario` TEXT NOT NULL,
                `data` TEXT NOT NULL,
                `foiTomado` INTEGER NOT NULL,
                `observacao` TEXT,
                `sincronizado` INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(`medicamentoId`) REFERENCES `medicamentos`(`id`) ON DELETE NO ACTION
            )
        """.trimIndent())
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS medicamentos_v2 (
                id INTEGER NOT NULL PRIMARY KEY,
                nome TEXT NOT NULL,
                compostoAtivo TEXT NOT NULL,
                dosagem TEXT NOT NULL,
                freq_frequenciaUsoTipo TEXT NOT NULL,
                freq_usoContinuo INTEGER NOT NULL,
                freq_horariosEspecificos TEXT NOT NULL,
                freq_intervaloHoras INTEGER,
                freq_primeiroHorario TEXT,
                freq_dataInicio TEXT,
                freq_dataTermino TEXT
            )
        """.trimIndent())
    }
}