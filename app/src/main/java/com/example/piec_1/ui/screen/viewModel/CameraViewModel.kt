package com.example.piec_1.ui.screen.viewModel

import android.app.Application
import android.graphics.Rect
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.example.piec_1.data.remote.ApiClient
import com.example.piec_1.data.remote.ScanResponse
import com.example.piec_1.domain.model.Medicamento
import com.example.piec_1.domain.service.CameraService

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    // Inicialização manual do ApiService através do seu ApiClient
    private val apiService = ApiClient().apiService

    // Inicialização do CameraService passando o contexto e o serviço de API
    private val cameraService = CameraService(getApplication(), apiService)

    // LiveData para o resultado bruto da API FastAPI
    private val _scanResult = MutableLiveData<ScanResponse?>()
    val scanResult: LiveData<ScanResponse?> = _scanResult

    // LiveData para o objeto Medicamento (usado na TelaConfirmacao)
    private val _medicamento = MutableLiveData<Medicamento?>()
    val medicamento: LiveData<Medicamento?> = _medicamento

    // LiveDatas para o Overlay da Câmera (Retângulo de detecção)
    private val _framePosition = MutableLiveData<Rect?>()
    val framePosition: LiveData<Rect?> get() = _framePosition

    private val _isRectangleDetected = MutableLiveData(false)
    val isRectangleDetected: LiveData<Boolean> get() = _isRectangleDetected

    // Estado de carregamento para a UI
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    /**
     * Inicia o stream da câmera e a análise de frames para detectar o objeto
     */
    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        cameraService.startCamera(previewView, lifecycleOwner) { detected, detectedRect ->
            _isRectangleDetected.postValue(detected)
            _framePosition.postValue(detectedRect)
        }
    }

    /**
     * Tira a foto e envia para o servidor FastAPI (/scan)
     */
    fun capturePhoto(navController: NavController) {
        _isLoading.postValue(true)

        cameraService.capturePhoto { response: ScanResponse? ->
            _isLoading.postValue(false)
            val info = response?.data
            if (info != null) {
                _scanResult.postValue(response)

                val novoMedicamento = Medicamento(
                    id = 0,
                    nome = info.nome ?: "Não identificado",
                    compostoAtivo = info.agente_ativo ?: "Não identificado",
                    dosagem = info.dosagem ?: "N/A",
                    quantidade = info.quantidade ?: "0",
                    validade = info.validade ?: "",
                    horarios = emptyList(),
                    usoContinuo = false,
                    sincronizado = false
                )

                _medicamento.postValue(novoMedicamento)

                // Navega para a tela de revisão de dados
                navController.navigate("TelaConfirmacao")
            } else {
                Log.e("CameraVM", "Falha ao processar imagem ou erro de conexão.")
                // Aqui você poderia atualizar um LiveData de erro para mostrar um Toast na View
            }
        }
    }

    /**
     * Atualiza o medicamento temporário após edição do usuário na TelaConfirmacao
     */
    fun atualizarMedicamento(novoMedicamento: Medicamento) {
        _medicamento.value = novoMedicamento
    }
}