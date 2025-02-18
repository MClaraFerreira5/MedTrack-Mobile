package com.example.piec_1.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

class DetectionService(private val context: Context) {

    private val objectDetector: ObjectDetector

    init {
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .build()

        objectDetector = ObjectDetection.getClient(options)
    }

    fun detectObjects(
        image: Bitmap,
        previewWidth: Int,
        previewHeight: Int,
        onDetectionResult: (Rect) -> Unit
    ) {
        val inputImage = InputImage.fromBitmap(image, 0)

        objectDetector.process(inputImage)
            .addOnSuccessListener { detectedObjects ->
                if (detectedObjects.isNotEmpty()) {
                    val objectBounds = detectedObjects.first().boundingBox
                    val adjustedBounds = adjustBoundingBox(
                        objectBounds,
                        image.width,
                        image.height,
                        previewWidth,
                        previewHeight
                    )
                    onDetectionResult(adjustedBounds)
                }
            }
            .addOnFailureListener { e ->
                Log.e("DetectionService", "Erro na detecção: ${e.message}")
            }
    }

    private fun adjustBoundingBox(
        boundingBox: Rect,
        imageWidth: Int,
        imageHeight: Int,
        previewWidth: Int,
        previewHeight: Int
    ): Rect {
        val scaleX = previewWidth.toFloat() / imageWidth
        val scaleY = previewHeight.toFloat() / imageHeight

        return Rect(
            (boundingBox.left * scaleX).toInt(),
            (boundingBox.top * scaleY).toInt(),
            (boundingBox.right * scaleX).toInt(),
            (boundingBox.bottom * scaleY).toInt()
        )
    }
}