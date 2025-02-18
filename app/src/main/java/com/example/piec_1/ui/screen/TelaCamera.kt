package com.example.piec_1.ui.screen

import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.piec_1.viewModel.CameraViewModel

@Composable
fun TelaCamera(
    navController: NavController,
    viewModel: CameraViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val recognizedText = remember { mutableStateOf("") }
    val framePosition = viewModel.framePosition.observeAsState().value

    LaunchedEffect(Unit) {
        viewModel.startCamera(previewView, lifecycleOwner)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 48.dp)
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-40).dp)
                .clickable {
                    viewModel.capturePhoto(
                        onImageCaptured = { imagePath ->
                            navController.popBackStack()
                        },
                        onTextRecognized = { text ->
                            recognizedText.value = text

                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val outerRadius = size.maxDimension / 2
                val innerRadius = outerRadius * 0.7f

                drawCircle(
                    color = Color.White,
                    radius = outerRadius,
                    style = Stroke(width = 8.dp.toPx())
                )

                drawCircle(
                    color = Color.White,
                    radius = innerRadius,
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            framePosition?.let { rect ->
                drawRect(
                    color = Color.White,
                    topLeft = Offset(rect.left.toFloat(), rect.top.toFloat()),
                    size = Size(rect.width().toFloat(), rect.height().toFloat()),
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }
    }
}

