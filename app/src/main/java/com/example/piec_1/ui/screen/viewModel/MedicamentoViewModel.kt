package com.example.piec_1.ui.screen.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.piec_1.data.local.AppDatabase
import com.example.piec_1.utils.exceptions.ConfirmacaoExistenteException
import com.example.piec_1.utils.exceptions.MedicamentoNaoEncontradoException
import com.example.piec_1.utils.exceptions.TokenNaoEncontradoException
import com.example.piec_1.domain.model.Confirmacao
import com.example.piec_1.domain.model.DadosConfirmacaoRequest
import com.example.piec_1.domain.model.Medicamento
import com.example.piec_1.domain.service.AuthService
import com.example.piec_1.data.remote.ApiClient
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MedicamentoViewModel(application: Application) : AndroidViewModel(application) {
    private val medicamentoDao = AppDatabase.getDatabase(application).medicamentoDao()
    private val confirmacaoDao = AppDatabase.getDatabase(application).confirmacaoDao()
    private val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()
    private val apiService = ApiClient().apiService
    private val authService = AuthService(application)

    private val _uiState = MutableLiveData<MedicamentoUIState>(MedicamentoUIState.Idle)

    sealed class MedicamentoUIState {
        object Idle : MedicamentoUIState()
        object Loading : MedicamentoUIState()
        data class Success(val message: String) : MedicamentoUIState()
        data class Error(val message: String) : MedicamentoUIState()
    }

//    suspend fun encontrarMedicamento(nome: String, compostoAtivo: String): Medicamento? {
//        val termoNormalizado = nome
//            .trim()
//            .replace(Regex("[^a-zA-Z0-9]"), "")
//            .lowercase()
//
//        val compostoNormalizado = compostoAtivo
//            .trim()
//            .replace(Regex("[^a-zA-Z0-9]"), "")
//            .lowercase()
//
//        return medicamentoDao.buscarMedicamentoFlexivel(
//            nome = "%$termoNormalizado%",
//            compostoAtivo = "%$compostoNormalizado%",
//            termoBusca = termoNormalizado
//        ) ?: throw MedicamentoNaoEncontradoException()
//    }

    fun confirmarMedicamento(
        medicamentoCapturado: Medicamento,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = MedicamentoUIState.Loading

            try {
                val token = authService.getToken() ?: throw TokenNaoEncontradoException()

                // 1. Busca correspondência na API
                val medicamentosApi = medicamentoDao.getMedicamentos()
                val medicamentoCorrespondente = encontrarMedicamentoCorrespondente(
                    medicamentoCapturado,
                    medicamentosApi
                ) ?: throw MedicamentoNaoEncontradoException()

                // 2. Processa confirmação
                processarConfirmacao(medicamentoCorrespondente, token)

                _uiState.value = MedicamentoUIState.Success("Medicamento confirmado!")
                onSuccess()

            } catch (_: TokenNaoEncontradoException) {
                _uiState.value = MedicamentoUIState.Error("Sessão expirada")
                onError("Sessão expirada. Faça login novamente.")

            } catch (_: MedicamentoNaoEncontradoException) {
                _uiState.value = MedicamentoUIState.Error("Medicamento não cadastrado")
                onError("Medicamento não cadastrado. Cadastre-o primeiro.")

            } catch (_: ConfirmacaoExistenteException) {
                _uiState.value = MedicamentoUIState.Error("Confirmação duplicada")
                onError("Já existe uma confirmação para este horário.")

            } catch (e: Exception) {
                _uiState.value = MedicamentoUIState.Error("Erro inesperado")
                onError("Erro ao confirmar: ${e.message ?: "Tente novamente"}")
            }
        }
    }

    private fun encontrarMedicamentoCorrespondente(
        medicamentoCapturado: Medicamento,
        medicamentosApi: List<Medicamento>
    ): Medicamento? {
        return medicamentosApi.firstOrNull { apiMed ->
            normalizarString(apiMed.nome) == normalizarString(medicamentoCapturado.nome) &&
                    normalizarString(apiMed.compostoAtivo) == normalizarString(medicamentoCapturado.compostoAtivo) &&
                    normalizarDosagem(apiMed.dosagem) == normalizarDosagem(medicamentoCapturado.dosagem)
        }
    }

    private fun normalizarDosagem(dosagem: String): String {
        return dosagem.replace(" ", "").lowercase()
    }

    private fun normalizarString(texto: String): String {
        return texto.trim()
            .replace(Regex("[^a-zA-Z0-9]"), "")
            .lowercase()
    }

    private suspend fun processarConfirmacao(medicamento: Medicamento, token: String) {
        confirmacaoDao.deleteAll()

        // 1. Encontrar horário adequado
        val (horarioSelecionado, _) = encontrarHorarioMaisProximo(medicamento.horarios)
        val dataAtual = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE).toString()

        println("DEBUG: Buscando confirmação para:")
        println("MedicamentoID: ${medicamento.id}")
        println("Data: $dataAtual")
        println("Horário: $horarioSelecionado")

        val confirmacaoExistente = confirmacaoDao.getConfirmacao(medicamento.id, dataAtual, horarioSelecionado)
        println("DEBUG: Confirmação encontrada: ${confirmacaoExistente?.toString() ?: "Nenhuma"}")

        if (confirmacaoExistente != null) {
            throw ConfirmacaoExistenteException()
        }

        // 3. Criar confirmação local
        val confirmacao = Confirmacao(
            medicamentoId = medicamento.id,
            horario = horarioSelecionado,
            data = dataAtual,
            foiTomado = true
        )
        confirmacaoDao.insert(confirmacao)

        val request = DadosConfirmacaoRequest(
            usuarioId = getUsuarioId(),
            medicamentoId = medicamento.id,
            horario = horarioSelecionado,
            data = dataAtual,
            foiTomado = true,
            observacao = null
        )

        Log.d("Confirmacao", "Request: $request")

        // 4. Sincronizar com API
        val response = apiService.confirmarMedicamento("Bearer $token", request)

        Log.d("Confirmacao", "Response - Código: ${response.code()}, Body: ${response.body()}")

        if (!response.isSuccessful) {
            throw IOException(response.errorBody()?.string() ?: "Erro na API")
        }

        // 5. Atualizar status de sincronização
        confirmacaoDao.update(confirmacao.copy(sincronizado = true))
    }

    private fun encontrarHorarioMaisProximo(horarios: List<String>): Pair<String, List<String>> {
        val horaAtual = LocalTime.now()
        val horariosOrdenados = horarios.sortedBy { LocalTime.parse(it) }

        // Encontra o primeiro horário que ainda não passou
        val horarioSelecionado = horariosOrdenados.firstOrNull {
            LocalTime.parse(it).isAfter(horaAtual.minusMinutes(30))
        } ?: horariosOrdenados.lastOrNull() ?: "00:00"

        return Pair(horarioSelecionado, horariosOrdenados)
    }

    private suspend fun getUsuarioId(): Long {
        return usuarioDao.getUsuario().id
    }

}