package com.example.piec_1.data.repository

import android.content.Context
import com.example.piec_1.data.PreferencesManager
import com.example.piec_1.data.local.AppDatabase
import com.example.piec_1.data.remote.ApiClient
import com.example.piec_1.data.remote.ApiService
import com.example.piec_1.domain.model.LoginRequest
import com.example.piec_1.domain.model.Medicamento
import com.example.piec_1.domain.model.MedicamentoDomain
import com.example.piec_1.domain.model.Usuario
import com.example.piec_1.domain.model.mappers.toEntity
import com.example.piec_1.domain.model.mappers.toLegacyMedicamento
import com.example.piec_1.utils.notifications.NotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class MedTrackRepository(
    context: Context,
    private val apiService: ApiService = ApiClient().apiService,
    database: AppDatabase = AppDatabase.getDatabase(context)
) {
    private val appContext = context.applicationContext
    private val usuarioDao = database.usuarioDao()
    private val medicamentoDao = database.medicamentoDao()
    private val medicamentoV2Dao = database.medicamentoV2Dao()
    private val notificationScheduler = NotificationScheduler(appContext)

    suspend fun login(username: String, password: String): LoginData = withContext(Dispatchers.IO) {
        val response = apiService.login(LoginRequest(username, password))

        if (!response.isSuccessful) {
            throw LoginException("Usuario ou senha invalidos")
        }

        val token = response.body()?.token
            ?: throw LoginException("Token invalido")

        PreferencesManager.saveToken(appContext, token)
        fetchData(token)
    }

    private suspend fun fetchData(token: String): LoginData {
        val authHeader = "Bearer $token"
        val usuario = buscarUsuario(authHeader)
        val medicamentosDomain = buscarMedicamentos(authHeader)

        usuarioDao.insert(usuario)
        medicamentoV2Dao.insertAll(medicamentosDomain.map { it.toEntity() })

        val medicamentosLegados = medicamentosDomain.map { it.toLegacyMedicamento() }
        medicamentoDao.insertAll(medicamentosLegados)
        agendarNotificacoes(medicamentosLegados)

        return LoginData(
            token = token,
            usuario = usuario,
            medicamentos = medicamentosLegados
        )
    }

    private suspend fun buscarUsuario(authHeader: String): Usuario {
        val response = apiService.getUsuario(authHeader)

        if (!response.isSuccessful) {
            throw IOException(response.errorBody()?.string() ?: "Erro ao buscar usuario")
        }

        return response.body() ?: throw IOException("Usuario nao encontrado")
    }

    private suspend fun buscarMedicamentos(authHeader: String): List<MedicamentoDomain> {
        val response = apiService.getMedicamentos(authHeader)

        if (!response.isSuccessful) {
            throw IOException(response.errorBody()?.string() ?: "Erro ao buscar medicamentos")
        }

        return response.body().orEmpty()
    }

    private fun agendarNotificacoes(medicamentos: List<Medicamento>) {
        medicamentos.forEach { medicamento ->
            notificationScheduler.agendarNotificacao(medicamento)
        }
    }
}

data class LoginData(
    val token: String,
    val usuario: Usuario,
    val medicamentos: List<Medicamento>
)

class LoginException(message: String) : Exception(message)
