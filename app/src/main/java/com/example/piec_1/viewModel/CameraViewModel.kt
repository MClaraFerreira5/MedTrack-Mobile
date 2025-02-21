package com.example.piec_1.viewModel

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.piec_1.model.Medicamento
import com.example.piec_1.service.CameraService
import com.example.piec_1.service.DetectionService
import com.example.piec_1.service.OCRService

class CameraViewModel(application: Application): AndroidViewModel(application) {
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
    val framePosition: LiveData<Rect> get() = _framePosition as LiveData<Rect>

    private var previewWidth: Int = 0
    private var previewHeight: Int = 0


    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        previewWidth = previewView.width
        previewHeight = previewView.height

        cameraService.startCamera(previewView, lifecycleOwner) { detectedRect ->
            _framePosition.postValue(detectedRect)
        }
    }

    fun capturePhoto(onImageCaptured: (String) -> Unit,  medicamentoExtraido: (Medicamento) -> Unit) {
        cameraService.capturePhoto( { imagePath ->
            _photoPath.postValue(imagePath)
            onImageCaptured(imagePath)
            Log.d("CameraX", "Imagem Capturada")

            val bitmap = BitmapFactory.decodeFile(imagePath)
            detectionService.detectObjects(bitmap, previewWidth, previewHeight) { objectBounds ->
                _framePosition.value = objectBounds
            }

        }, { medicamento ->

            _medicamento.postValue(medicamento)

            medicamentoExtraido(medicamento)
        })
    }
}