package com.example.piec_1

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import java.io.File

class CameraViewModel(application: Application): AndroidViewModel(application) {
    private var imageCapture : ImageCapture? = null

    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(getApplication())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)

                Log.d("CameraX", "Câmera inicializada com sucesso!")

            } catch (e: Exception) {
                Log.e("CameraX", "Erro ao iniciar a câmera: ${e.message}")
            }


        }, ContextCompat.getMainExecutor(getApplication()))
    }

    fun capturePhoto(context: Context, onImageCaptured: (String) -> Unit ) {
        val file = File(context.externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture?.takePicture(outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onImageCaptured(file.absolutePath)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Erro ao capturar foto: ${exception.message}")
                }
            })


    }


}