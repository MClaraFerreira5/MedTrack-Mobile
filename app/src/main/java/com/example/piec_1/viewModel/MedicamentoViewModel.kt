package com.example.piec_1.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.piec_1.database.AppDatabase
import com.example.piec_1.model.Confirmacao
import com.example.piec_1.model.DadosConfirmacaoRequest
import com.example.piec_1.model.Medicamento
import com.example.piec_1.service.api.ApiClient
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class MedicamentoViewModel(application: Application) : AndroidViewModel(application) {
    private val medicamentoDao = AppDatabase.getDatabase(application).medicamentoDao()
    private val confirmacaoDao = AppDatabase.getDatabase(application).confirmacaoDao()
    private val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()
    private val apiService = ApiClient().apiService
    private val sharedPref = application.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun inserir(medicamento: Medicamento) {
        viewModelScope.launch {
            medicamentoDao.insertAll(listOf(medicamento))
        }
    }

    fun listarTodos(onResult: (List<Medicamento>) -> Unit) {
        viewModelScope.launch {
            val medicamentos = medicamentoDao.getMedicamentos() ?: emptyList()
            onResult(medicamentos)
        }
    }

    suspend fun encontrarMedicamento(nome: String, compostoAtivo: String): Medicamento? {
        return medicamentoDao.getMedicamentoPorNomeOuComposto(nome, compostoAtivo)
    }

    // Função principal para confirmar medicamento
    fun confirmarMedicamento(
        nomeMedicamento: String,
        compostoAtivo: String,
        dosagem: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1. Encontrar e validar medicamento
                val medicamento = encontrarMedicamento(nomeMedicamento, compostoAtivo)
                    ?: throw Exception("Medicamento não encontrado")

                // 2. Encontrar horário adequado
                val (horarioSelecionado, _) = encontrarHorarioMaisProximo(medicamento.horarios)

                // 3. Verificar confirmação existente
                val dataAtual = LocalDate.now().toString()
                if (confirmacaoDao.getConfirmacao(medicamento.id, dataAtual, horarioSelecionado) != null) {
                    throw Exception("Já existe uma confirmação para este horário")
                }

                // 4. Criar confirmação local
                val confirmacao = Confirmacao(
                    medicamentoId = medicamento.id,
                    horario = horarioSelecionado,
                    data = dataAtual,
                    foiTomado = true
                )
                confirmacaoDao.insert(confirmacao)

                // 5. Tentar sincronizar IMEDIATAMENTE
                val token = sharedPref.getString("token", null) ?: throw Exception("Medicamento não Cadastrado")

                val response = apiService.confirmarMedicamento(
                    "Bearer $token",
                    DadosConfirmacaoRequest(
                        usuarioId = getUsuarioId(),
                        medicamentoId = medicamento.id,
                        horario = horarioSelecionado,
                        data = dataAtual,
                        foiTomado = true,
                        observacao = null
                    )
                )

                if (!response.isSuccessful) {
                    throw Exception(response.errorBody()?.string() ?: "Erro ao confirmar")
                }

                // 6. Atualizar como sincronizado se sucesso
                confirmacaoDao.update(confirmacao.copy(sincronizado = true))

                // 7. Notificar sucesso
                onSuccess()

            } catch (e: Exception) {
                onError(e.message ?: "Erro ao confirmar medicamento")
            }
        }
    }

    private suspend fun encontrarHorarioMaisProximo(horarios: List<String>): Pair<String, List<String>> {
        val horaAtual = LocalTime.now()
        val horariosOrdenados = horarios.sortedBy { LocalTime.parse(it) }

        // Encontra o primeiro horário que ainda não passou
        val horarioSelecionado = horariosOrdenados.firstOrNull {
            LocalTime.parse(it).isAfter(horaAtual.minusMinutes(30))
        } ?: horariosOrdenados.lastOrNull() ?: "00:00"

        return Pair(horarioSelecionado, horariosOrdenados)
    }

    private fun sincronizarConfirmacoesPendentes() {
        viewModelScope.launch {
            try {
                val token = sharedPref.getString("token", null) ?: return@launch
                val confirmacoes = confirmacaoDao.getConfirmacoesNaoSincronizadas()

                for (confirmacao in confirmacoes) {
                    val response = apiService.confirmarMedicamento(
                        "Bearer $token",
                        DadosConfirmacaoRequest(
                            usuarioId = getUsuarioId(),
                            medicamentoId = confirmacao.medicamentoId,
                            horario = confirmacao.horario,
                            data = confirmacao.data,
                            foiTomado = confirmacao.foiTomado,
                            observacao = confirmacao.observacao
                        )
                    )

                    if (response.isSuccessful) {
                        confirmacaoDao.update(confirmacao.copy(sincronizado = true))
                    }
                }
            } catch (e: Exception) {
                Log.e("MedicamentoViewModel", "Erro ao sincronizar confirmações", e)
            }
        }
    }

    private suspend fun getUsuarioId(): Long {
        return usuarioDao.getUsuario().id
    }
}