package com.example.piec_1.viewModel

import android.app.Application
import android.graphics.Rect
import androidx.camera.view.PreviewView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.piec_1.model.Medicamento
import com.example.piec_1.service.CameraService
import com.example.piec_1.service.DetectionService

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val cameraService = CameraService(context)
    private val detectionService = DetectionService(context)

    private val _photoPath = MutableLiveData<String?>()
    val photoPath: LiveData<String?> = _photoPath

    private val _recognizedText = MutableLiveData<String>()
    val recognizedText: LiveData<String> get() = _recognizedText

    private val _medicamento = MutableLiveData<Medicamento?>()
    val medicamento: LiveData<Medicamento?> = _medicamento

    private val _framePosition = MutableLiveData<Rect?>()
    val framePosition: LiveData<Rect?> get() = _framePosition

    private val _isRectangleDetected = MutableLiveData<Boolean>(false)
    val isRectangleDetected: LiveData<Boolean> get() = _isRectangleDetected

    private var previewWidth: Int = 0
    private var previewHeight: Int = 0

    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        previewWidth = previewView.width
        previewHeight = previewView.height

        cameraService.startCamera(previewView, lifecycleOwner) { detected, detectedRect ->
            _isRectangleDetected.postValue(detected)
            _framePosition.postValue(detectedRect)
        }
    }

    fun capturePhoto(
        onImageCaptured: (String) -> Unit,
        medicamentoExtraido: (Medicamento) -> Unit
    ) {
        cameraService.capturePhoto({ imagePath ->
            _photoPath.postValue(imagePath)
            onImageCaptured(imagePath)
        }, { medicamento ->
            _medicamento.postValue(medicamento)
            medicamentoExtraido(medicamento)
        })
    }
}