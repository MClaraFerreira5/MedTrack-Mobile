package com.example.piec_1.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.Executors

class CameraService(
    private val context: Context
) {
    private val detectionService: DetectionService = DetectionService(context)
    private var imageCapture: ImageCapture? = null

    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner, onObjectDetected: (Rect?) -> Unit){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val previewWidth = previewView.width
            val previewHeight = previewView.height

            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                processFrame(imageProxy, previewWidth, previewHeight, onObjectDetected)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis, imageCapture)
            } catch (e: Exception) {
                Log.e("CameraX", "Erro ao iniciar câmera", e)
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

                onTextRecognized("${medicamento.nome}, ${medicamento.compostoAtivo}, ${medicamento.dosagem}")

            }
            .addOnFailureListener { e ->
                Log.e("MLKit", "Erro ao reconhecer texto", e)
            }
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun processFrame(
        imageProxy: ImageProxy,
        previewWidth: Int,
        previewHeight: Int,
        onObjectDetected: (Rect?) -> Unit
    ) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val bitmap = mediaImage.toBitmap(rotationDegrees)

            detectionService.detectObjects(bitmap, previewWidth, previewHeight) { objectBounds: Rect? ->
                onObjectDetected(objectBounds)
            }
        }

        imageProxy.close()
    }

    private fun Image.toBitmap(rotationDegrees: Int): Bitmap {
        val yuvBytes = yuv420ToNv21(this)
        val yuvImage = YuvImage(yuvBytes, ImageFormat.NV21, width, height, null)

        val out = ByteArrayOutputStream()
        val success = yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)

        if (!success) {
            Log.e("CameraX", "Erro ao comprimir YUV para JPEG")
            throw IllegalStateException("Erro ao comprimir imagem")
        }

        val imageBytes = out.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        if (bitmap == null) {
            Log.e("CameraX", "Falha ao converter a imagem para Bitmap")
            throw IllegalStateException("Falha na conversão para Bitmap")
        }

        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun yuv420ToNv21(image: Image): ByteArray {
        val yPlane = image.planes[0].buffer
        val uPlane = image.planes[1].buffer
        val vPlane = image.planes[2].buffer

        val ySize = yPlane.remaining()
        val uSize = uPlane.remaining()
        val vSize = vPlane.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        // Copia os valores do plano Y
        yPlane.get(nv21, 0, ySize)

        // Copia os valores dos planos UV intercalados para NV21
        val uvPixelStride = image.planes[1].pixelStride
        val uvRowStride = image.planes[1].rowStride
        var pos = ySize

        for (row in 0 until image.height / 2) {
            for (col in 0 until image.width / 2) {
                val uIndex = row * uvRowStride + col * uvPixelStride
                val vIndex = row * uvRowStride + col * uvPixelStride

                nv21[pos++] = vPlane[vIndex]  // V
                nv21[pos++] = uPlane[uIndex]  // U
            }
        }

        return nv21
    }


}

