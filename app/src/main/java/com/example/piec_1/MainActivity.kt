package com.example.piec_1


import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.piec_1.navigation.AppNavigation
import com.example.piec_1.ui.theme.PIEC1Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        createNotificationChannel()

        setContent {
            PIEC1Theme {
                val isPermissionGranted = remember { mutableStateOf(false) }

                if (isPermissionGranted.value) {
                    AppNavigation()
                } else {
                    RequestPermission { isGranted ->
                        isPermissionGranted.value = isGranted
                    }
                }
            }
        }
    }

    @Composable
    private fun RequestPermission(onPermissionResult: (Boolean) -> Unit) {
        val context = LocalContext.current
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            onPermissionResult(isGranted)
            if (!isGranted) {
                Toast.makeText(context, "Permissão da câmera negada!", Toast.LENGTH_LONG).show()
            }
        }

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                onPermissionResult(true)
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "medicamento_channel",
            "Lembretes de Medicamentos",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificações urgentes para tomar medicamentos"
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
