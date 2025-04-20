package com.example.piec_1


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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
import com.example.piec_1.notifications.NotificationHelper
import com.example.piec_1.ui.theme.PIEC1Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this)

        if (!areNotificationsEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Toast.makeText(
                    this,
                    "Por favor, habilite as notificações nas configurações do aplicativo",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

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
        val permissions = remember {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else {
                arrayOf(Manifest.permission.CAMERA)
            }
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsMap ->
            val allGranted = permissionsMap.values.all { it }
            onPermissionResult(allGranted)
            if (!allGranted) {
                Toast.makeText(
                    context,
                    "Algumas permissões necessárias foram negadas!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        LaunchedEffect(Unit) {
            val allGranted = permissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }

            if (allGranted) {
                onPermissionResult(true)
            } else {
                permissionLauncher.launch(permissions)
            }
        }
    }

    fun Context.areNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
