package com.example.piec_1.service

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

class CameraService(
    private val context: Context
) {
    private var imageCapture: ImageCapture? = null

    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner, onCameraReady: () -> Unit){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

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
                onCameraReady()
            } catch (e: Exception) {
                Log.e("CameraX", "Erro ao iniciar a câmera: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun capturePhoto(onImageCaptured: (String) -> Unit, onTextRecognized: (String) -> Unit){
        val file = File(context.externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture?.takePicture(outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onImageCaptured(file.absolutePath)
                    processImage(file, onTextRecognized)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Erro ao capturar foto: ${exception.message}")
                }
            })
    }

    fun processImage(file: File, onTextRecognized: (String) -> Unit) {
        val image = InputImage.fromFilePath(context, Uri.fromFile(file))

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val extractedText = visionText.text
                Log.d("MLKit", "Texto extraído: $extractedText")

                val ocrService = OCRService()
                val medicamento = ocrService.extrairMedicamentoInfo(extractedText)

                Log.d("OCR", "Medicamento Nome: ${medicamento.nome}")
                Log.d("OCR", "Composto Ativo: ${medicamento.compostoAtivo}")
                Log.d("OCR", "Dosagem: ${medicamento.dosagem}")

                onTextRecognized("${medicamento.nome}, ${medicamento.compostoAtivo}, ${medicamento.dosagem}")

            }
            .addOnFailureListener { e ->
                Log.e("MLKit", "Erro ao reconhecer texto", e)
            }
    }
}