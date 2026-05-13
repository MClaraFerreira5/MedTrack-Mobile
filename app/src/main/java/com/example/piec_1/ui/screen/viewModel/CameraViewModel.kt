package com.example.piec_1.ui.screen.viewModel

import android.graphics.Rect
import android.net.Uri
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.piec_1.data.remote.MedicamentoData
import com.example.piec_1.data.remote.ScanResponse
import com.example.piec_1.data.repository.MedTrackRepository
import com.example.piec_1.domain.model.Medicamento
import com.example.piec_1.domain.service.CameraService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository: MedTrackRepository,
    private val cameraService: CameraService
) : ViewModel() {

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

    fun capturePhoto(navController: NavController, isOnline: Boolean) {
        if (!isOnline) {
            _showOfflineDialog.postValue(true)
            return
        }

        _isLoading.postValue(true)
        cameraService.capturePhotoOnly { uri ->
            if (uri != null) {
                processOnlinePhoto(uri, navController)
            } else {
                _isLoading.postValue(false)
                Log.e("CameraVM", "Erro ao capturar imagem")
            }
        }
    }

    fun processOfflinePhoto() {
        _isLoading.postValue(true)
        cameraService.capturePhotoOnly { uri ->
            if (uri != null) {
                saveForLater(uri)
            } else {
                _isLoading.postValue(false)
                Log.e("CameraVM", "Erro ao capturar imagem offline")
            }
        }
    }

    private fun processOnlinePhoto(uri: Uri, navController: NavController) {
        viewModelScope.launch {
            try {
                val file = File(uri.path.orEmpty())
                val response = repository.scanMedicamento(file)

                _isLoading.postValue(false)

                if (response?.data != null) {
                    _scanResult.postValue(response)
                    _medicamento.postValue(response.data.toMedicamento())
                    navController.navigate("TelaConfirmacao")
                } else {
                    Log.e("CameraVM", "Erro na analise da IA")
                }
            } catch (e: Exception) {
                _isLoading.postValue(false)
                Log.e("CameraVM", "Erro no processamento online: ${e.message}", e)
            }
        }
    }

    fun saveForLater(uri: Uri) {
        viewModelScope.launch {
            try {
                repository.salvarScanOffline(uri)
                _showOfflineDialog.postValue(false)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                Log.e("CameraVM", "Erro ao salvar scan offline: ${e.message}", e)
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

    private fun MedicamentoData.toMedicamento() = Medicamento(
        id = 0,
        nome = nome ?: "Nao identificado",
        compostoAtivo = agente_ativo ?: "Nao identificado",
        dosagem = dosagem ?: "N/A",
        quantidade = quantidade ?: "0",
        validade = validade ?: "",
        horarios = emptyList(),
        usoContinuo = false,
        sincronizado = false
    )
}
