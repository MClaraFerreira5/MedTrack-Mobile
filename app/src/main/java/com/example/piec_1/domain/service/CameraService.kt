package com.example.piec_1.domain.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import android.util.Log
import androidx.annotation.OptIn
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
import com.example.piec_1.data.SharedPreferencesHelper
import com.example.piec_1.data.remote.ApiService
import com.example.piec_1.data.remote.ScanResponse
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.Executors

class CameraService(
    private val context: Context,
    private val apiService: ApiService
) {

    private val detectionService: DetectionService = DetectionService()
    private var imageCapture: ImageCapture? = null

    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        onObjectDetected: (Boolean, Rect?) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { it.surfaceProvider = previewView.surfaceProvider }

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

    fun capturePhoto(onScanResult: (ScanResponse?) -> Unit) {
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${System.currentTimeMillis()}.jpg"
        )
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture?.takePicture(outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    if (!isWifiConnected()) {
                        Log.w("CameraX", "Sem Wi-Fi. Abortando scan online.")
                        onScanResult(null)
                        return
                    }

                    MainScope().launch {
                        val resultado = enviarParaScan(file)
                        onScanResult(resultado)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Erro ao capturar foto: ${exception.message}")
                    onScanResult(null)
                }
            })
    }

    suspend fun enviarParaScan(file: File): ScanResponse? {
        val token = SharedPreferencesHelper.getToken(context) ?: return null

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        return try {
            val response = apiService.scanMedicamento("Bearer $token", body)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("Scan", "Erro na API: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("Scan", "Erro detalhado: ${e.message}") // Isso vai te dizer se é erro de JSON!
            Log.e("Scan", "Falha na conexão: ${e.message}")
            null
        }
    }

    private fun isWifiConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processFrame(
        imageProxy: ImageProxy,
        previewWidth: Int,
        previewHeight: Int,
        onObjectDetected: (Boolean, Rect?) -> Unit
    ) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val bitmap = mediaImage.toBitmap(rotationDegrees)

            detectionService.detectObjects(bitmap, previewWidth, previewHeight) { detected, objectBounds: Rect? ->
                onObjectDetected(detected, objectBounds)
            }
        }
        imageProxy.close()
    }

    private fun Image.toBitmap(rotationDegrees: Int): Bitmap {
        val yuvBytes = yuv420ToNv21(this)
        val yuvImage = YuvImage(yuvBytes, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
        val imageBytes = out.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
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
        yPlane.get(nv21, 0, ySize)
        val uvPixelStride = image.planes[1].pixelStride
        val uvRowStride = image.planes[1].rowStride
        var pos = ySize
        for (row in 0 until image.height / 2) {
            for (col in 0 until image.width / 2) {
                val vIndex = row * uvRowStride + col * uvPixelStride
                val uIndex = row * uvRowStride + col * uvPixelStride
                nv21[pos++] = vPlane[vIndex]
                nv21[pos++] = uPlane[uIndex]
            }
        }
        return nv21
    }
}