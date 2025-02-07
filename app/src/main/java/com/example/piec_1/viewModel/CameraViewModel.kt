package com.example.piec_1.viewModel

import android.app.Application
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.piec_1.service.CameraService

class CameraViewModel(application: Application): AndroidViewModel(application) {
    private val cameraService = CameraService(application.applicationContext)

    private val _photoPath = MutableLiveData<String?>()
    val photoPath: LiveData<String?> = _photoPath

    private val _recognizedText = MutableLiveData<String>()
    val recognizedText: LiveData<String> get() = _recognizedText


    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        cameraService.startCamera(previewView, lifecycleOwner) {
            Log.d("CameraX", "Câmera inicializada com sucesso!")
        }
    }

    fun capturePhoto(onImageCaptured: (String) -> Unit,  onTextRecognized: (String) -> Unit) {
        cameraService.capturePhoto( { path ->
            _photoPath.postValue(path)
            onImageCaptured(path)
            Log.d("CameraX", "Imagem Capturada")

        }, { text ->
            Log.d("OCR", "Texto processado: $text")

            _recognizedText.postValue(text)
            onTextRecognized(text)
        })
    }
}