// CameraViewModel.kt - Versão corrigida
package com.example.piec_1.ui.screen.viewModel

import android.app.Application
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.piec_1.data.SharedPreferencesHelper
import com.example.piec_1.data.local.entity.ScanQueueItem
import com.example.piec_1.data.remote.ScanResponse
import com.example.piec_1.data.repository.ScanRepository
import com.example.piec_1.domain.model.Medicamento
import com.example.piec_1.domain.service.CameraService
import com.example.piec_1.domain.service.ScanUpload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = com.example.piec_1.data.remote.ApiClient().apiService
    private val repository = ScanRepository(application)
    private val cameraService = CameraService(getApplication(), apiService)

    private val _scanResult = MutableLiveData<ScanResponse?>()
    val scanResult: LiveData<ScanResponse?> = _scanResult

    private val _medicamento = MutableLiveData<Medicamento?>()
    val medicamento: LiveData<Medicamento?> = _medicamento

    private val _framePosition = MutableLiveData<Rect?>()
    val framePosition: LiveData<Rect?> get() = _framePosition

    private val _isRectangleDetected = MutableLiveData(false)
    val isRectangleDetected: LiveData<Boolean> get() = _isRectangleDetected

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _showOfflineDialog = MutableLiveData(false)
    val showOfflineDialog: LiveData<Boolean> get() = _showOfflineDialog

    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        cameraService.startCamera(previewView, lifecycleOwner) { detected, detectedRect ->
            _isRectangleDetected.postValue(detected)
            _framePosition.postValue(detectedRect)
        }
    }

    // Método principal - quando o usuário clica no botão de capturar
    fun capturePhoto(navController: NavController, isOnline: Boolean) {
        Log.d("CameraVM", "📸 Capturando foto. Online: $isOnline")

        // Se estiver offline, mostra diálogo e NÃO captura foto ainda
        if (!isOnline) {
            Log.d("CameraVM", "📱 Offline - mostrando diálogo")
            _showOfflineDialog.postValue(true)
            return
        }

        // Se estiver online, captura e processa
        _isLoading.postValue(true)

        cameraService.capturePhotoOnly { uri ->
            if (uri != null) {
                Log.d("CameraVM", "✅ Foto capturada com sucesso: $uri")
                processOnlinePhoto(uri, navController)
            } else {
                _isLoading.postValue(false)
                Log.e("CameraVM", "❌ Erro ao capturar imagem")
            }
        }
    }

    // Método chamado quando o usuário confirma o diálogo offline
    fun processOfflinePhoto() {
        Log.d("CameraVM", "📱 Processando foto offline")

        _isLoading.postValue(true)

        cameraService.capturePhotoOnly { uri ->
            if (uri != null) {
                Log.d("CameraVM", "✅ Foto capturada offline: $uri")
                saveForLater(uri)
            } else {
                _isLoading.postValue(false)
                Log.e("CameraVM", "❌ Erro ao capturar imagem offline")
            }
        }
    }

    private fun processOnlinePhoto(uri: Uri, navController: NavController) {
        viewModelScope.launch {
            try {
                val token = SharedPreferencesHelper.getToken(getApplication())
                if (token == null) {
                    _isLoading.postValue(false)
                    Log.e("CameraVM", "❌ Token não encontrado")
                    return@launch
                }

                val file = File(uri.path ?: "")
                val response = withContext(Dispatchers.IO) {
                    cameraService.uploadPhotoForScan(file, token)
                }

                _isLoading.postValue(false)

                if (response?.data != null) {
                    Log.d("CameraVM", "✅ Scan online bem sucedido: ${response.data.nome}")
                    _scanResult.postValue(response)
                    val novoMedicamento = Medicamento(
                        id = 0,
                        nome = response.data.nome ?: "Não identificado",
                        compostoAtivo = response.data.agente_ativo ?: "Não identificado",
                        dosagem = response.data.dosagem ?: "N/A",
                        quantidade = response.data.quantidade ?: "0",
                        validade = response.data.validade ?: "",
                        horarios = emptyList(),
                        usoContinuo = false,
                        sincronizado = false
                    )
                    _medicamento.postValue(novoMedicamento)
                    navController.navigate("TelaConfirmacao")
                } else {
                    Log.e("CameraVM", "❌ Erro na análise da IA")
                }
            } catch (e: Exception) {
                _isLoading.postValue(false)
                Log.e("CameraVM", "❌ Erro no processamento online: ${e.message}", e)
            }
        }
    }

    fun saveForLater(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("CameraVM", "💾 Salvando scan offline: $uri")

                val item = ScanQueueItem(
                    imagePath = uri.toString(),
                    status = "PENDENTE",
                    timestamp = System.currentTimeMillis()
                )
                repository.insertScanQueue(item)

                Log.d("CameraVM", "✅ Item salvo no queue com ID: ${item.id}")

                // Agenda o WorkManager para quando tiver Wi-Fi
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED) // Apenas Wi-Fi
                    .build()

                val scanWorkRequest = OneTimeWorkRequestBuilder<ScanUpload>()
                    .setConstraints(constraints)
                    .addTag("offline_scan_job")
                    .build()

                WorkManager.getInstance(getApplication()).enqueue(scanWorkRequest)

                Log.d("CameraVM", "⏰ WorkManager agendado com sucesso!")

                // Fecha o diálogo e o loading
                _showOfflineDialog.postValue(false)
                _isLoading.postValue(false)

            } catch (e: Exception) {
                Log.e("CameraVM", "❌ Erro ao salvar scan offline: ${e.message}", e)
                _isLoading.postValue(false)
            }
        }
    }

    fun dismissOfflineDialog() {
        _showOfflineDialog.postValue(false)
    }

    fun atualizarMedicamento(novoMedicamento: Medicamento) {
        _medicamento.value = novoMedicamento
    }
}