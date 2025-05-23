package com.example.piec_1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.piec_1.database.daos.ConfirmacaoDao
import com.example.piec_1.database.daos.MedicamentoDao
import com.example.piec_1.database.daos.NotificacaoDao
import com.example.piec_1.database.daos.UsuarioDao
import com.example.piec_1.database.migrations.MIGRATION_1_2
import com.example.piec_1.database.migrations.MIGRATION_2_3
import com.example.piec_1.database.migrations.MIGRATION_3_4
import com.example.piec_1.model.Confirmacao
import com.example.piec_1.model.Medicamento
import com.example.piec_1.model.Notificacao
import com.example.piec_1.model.Usuario

@Database(
    entities = [Usuario::class, Medicamento::class, Notificacao::class, Confirmacao::class],
    version = 4
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun medicamentoDao(): MedicamentoDao
    abstract fun notificacaoDao(): NotificacaoDao
    abstract fun confirmacaoDao(): ConfirmacaoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null


        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}