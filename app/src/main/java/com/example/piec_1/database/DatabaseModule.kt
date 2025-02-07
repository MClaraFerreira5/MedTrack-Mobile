package com.example.piec_1.database

import android.content.Context
import androidx.room.Room
import com.google.android.datatransport.runtime.dagger.Module
import com.google.android.datatransport.runtime.dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "medicamentos_db"
        ).build()
    }

    @Provides
    fun provideMedicamentoDao(appDatabase: AppDatabase): MedicamentoDao {
        return appDatabase.medicamentoDao()
    }
}