package com.example.piec_1.data.repository

import android.content.Context
import android.net.Uri
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.piec_1.data.PreferencesManager
import com.example.piec_1.data.local.AppDatabase
import com.example.piec_1.data.local.entity.ScanQueueItem
import com.example.piec_1.data.remote.ApiService
import com.example.piec_1.data.remote.MedicamentoData
import com.example.piec_1.data.remote.ScanResponse
import com.example.piec_1.domain.model.Confirmacao
import com.example.piec_1.domain.model.DadosConfirmacaoRequest
import com.example.piec_1.domain.model.LoginRequest
import com.example.piec_1.domain.model.Medicamento
import com.example.piec_1.domain.model.MedicamentoDomain
import com.example.piec_1.domain.model.Usuario
import com.example.piec_1.domain.model.mappers.toEntity
import com.example.piec_1.domain.model.mappers.toLegacyMedicamento
import com.example.piec_1.utils.exceptions.ConfirmacaoExistenteException
import com.example.piec_1.utils.exceptions.MedicamentoNaoEncontradoException
import com.example.piec_1.utils.exceptions.TokenNaoEncontradoException
import com.example.piec_1.domain.service.ScanUpload
import com.example.piec_1.utils.notifications.NotificationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MedTrackRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val apiService: ApiService,
    private val database: AppDatabase,
    @param:Named("ScanUrl") private val scanUrl: String,
    private val notificationScheduler: NotificationScheduler
) {
    private val appContext = context.applicationContext
    private val usuarioDao = database.usuarioDao()
    private val medicamentoDao = database.medicamentoDao()
    private val medicamentoV2Dao = database.medicamentoV2Dao()
    private val confirmacaoDao = database.confirmacaoDao()
    private val scanQueueDao = database.scanQueueDao()

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

        medicamentoDao.insertAll(medicamentosDomain.map { it.toLegacyMedicamento() })
        agendarNotificacoes(medicamentosDomain)

        return LoginData(
            token = token,
            usuario = usuario,
            medicamentos = medicamentosDomain
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

    private fun agendarNotificacoes(medicamentos: List<MedicamentoDomain>) {
        medicamentos.forEach { medicamento ->
            notificationScheduler.agendarNotificacao(medicamento)
        }
    }

    suspend fun confirmarMedicamento(medicamentoCapturado: Medicamento) = withContext(Dispatchers.IO) {
        val token = PreferencesManager.getToken(appContext) ?: throw TokenNaoEncontradoException()
        val medicamentoCorrespondente = encontrarMedicamentoCorrespondente(medicamentoCapturado)
            ?: throw MedicamentoNaoEncontradoException()

        processarConfirmacao(medicamentoCorrespondente, token)
    }

    private suspend fun encontrarMedicamentoCorrespondente(
        medicamentoCapturado: Medicamento
    ): Medicamento? {
        return medicamentoDao.getMedicamentos().firstOrNull { medicamentoSalvo ->
            normalizarString(medicamentoSalvo.nome) == normalizarString(medicamentoCapturado.nome) &&
                normalizarString(medicamentoSalvo.compostoAtivo) == normalizarString(medicamentoCapturado.compostoAtivo) &&
                normalizarDosagem(medicamentoSalvo.dosagem) == normalizarDosagem(medicamentoCapturado.dosagem)
        }
    }

    private suspend fun processarConfirmacao(medicamento: Medicamento, token: String) {
        val horarioSelecionado = encontrarHorarioMaisProximo(medicamento.horarios)
        val dataAtual = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val confirmacaoExistente = confirmacaoDao.getConfirmacao(
            medicamentoId = medicamento.id,
            data = dataAtual,
            horario = horarioSelecionado
        )

        if (confirmacaoExistente != null) {
            throw ConfirmacaoExistenteException()
        }

        val confirmacao = Confirmacao(
            medicamentoId = medicamento.id,
            horario = horarioSelecionado,
            data = dataAtual,
            foiTomado = true
        )
        val confirmacaoId = confirmacaoDao.insert(confirmacao)

        val request = DadosConfirmacaoRequest(
            usuarioId = usuarioDao.getUsuario().id,
            medicamentoId = medicamento.id,
            horario = horarioSelecionado,
            data = dataAtual,
            foiTomado = true,
            observacao = null
        )

        val response = apiService.confirmarMedicamento("Bearer $token", request)

        if (!response.isSuccessful) {
            throw IOException(response.errorBody()?.string() ?: "Erro na API")
        }

        confirmacaoDao.update(confirmacao.copy(id = confirmacaoId, sincronizado = true))
    }

    private fun encontrarHorarioMaisProximo(horarios: List<String>): String {
        val horaAtual = LocalTime.now()
        val horariosOrdenados = horarios.sortedBy { LocalTime.parse(it) }

        return horariosOrdenados.firstOrNull {
            LocalTime.parse(it).isAfter(horaAtual.minusMinutes(30))
        } ?: horariosOrdenados.lastOrNull() ?: "00:00"
    }

    private fun normalizarDosagem(dosagem: String): String {
        return dosagem.replace(" ", "").lowercase()
    }

    private fun normalizarString(texto: String): String {
        return texto.trim()
            .replace(Regex("[^a-zA-Z0-9]"), "")
            .lowercase()
    }

    suspend fun scanMedicamento(file: File): ScanResponse? = withContext(Dispatchers.IO) {
        val token = PreferencesManager.getToken(appContext) ?: throw TokenNaoEncontradoException()
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val response = apiService.scanMedicamento(scanUrl, "Bearer $token", body)

        if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun getPendingScans(): List<ScanQueueItem> = withContext(Dispatchers.IO) {
        scanQueueDao.getPendingScans()
    }

    suspend fun updateScanStatus(id: Int, status: String) = withContext(Dispatchers.IO) {
        scanQueueDao.updateStatus(id, status)
    }

    suspend fun uploadScanPendente(file: File): MedicamentoData? = withContext(Dispatchers.IO) {
        val token = PreferencesManager.getToken(appContext) ?: throw TokenNaoEncontradoException()
        val partNames = listOf("file", "image", "photo")

        for (partName in partNames) {
            val response = enviarImagemParaScan(file, token, partName)
            if (response?.data != null) {
                return@withContext response.data
            }
        }

        null
    }

    suspend fun salvarScanOffline(uri: Uri) = withContext(Dispatchers.IO) {
        scanQueueDao.insert(
            ScanQueueItem(
                imagePath = uri.toString(),
                status = "PENDENTE",
                timestamp = System.currentTimeMillis()
            )
        )
        agendarProcessamentoDeScansOffline()
    }

    private fun agendarProcessamentoDeScansOffline() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val scanWorkRequest = OneTimeWorkRequestBuilder<ScanUpload>()
            .setConstraints(constraints)
            .addTag("offline_scan_job")
            .build()

        WorkManager.getInstance(appContext).enqueue(scanWorkRequest)
    }

    private suspend fun enviarImagemParaScan(
        file: File,
        token: String,
        partName: String
    ): ScanResponse? {
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData(partName, file.name, requestFile)
        val response = apiService.scanMedicamento(scanUrl, "Bearer $token", body)

        return if (response.isSuccessful) response.body() else null
    }
}

data class LoginData(
    val token: String,
    val usuario: Usuario,
    val medicamentos: List<MedicamentoDomain>
)

class LoginException(message: String) : Exception(message)
