package com.example.piec_1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.example.piec_1.ui.navigation.NavigationManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.camera2.pipe.core.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.piec_1.data.remote.MedicamentoData
import com.example.piec_1.domain.model.Medicamento
import com.example.piec_1.ui.navigation.AppNavigation
import com.example.piec_1.utils.notifications.NotificationHelper
import com.example.piec_1.ui.theme.PIEC1Theme
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        var pendingMedicamentoFromNotification: Medicamento? = null
    }
    override fun onDestroy() {
        super.onDestroy()
        NavigationManager.clearController()
        NavigationManager.reset()
    }

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        processIntent(intent)
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
                    AppNavigation(
                        onNavControllerReady = { controller ->
                            NavigationManager.init(controller)
                        }
                    )
                } else {
                    RequestPermission { isGranted ->
                        isPermissionGranted.value = isGranted
                    }
                }
            }
        }}

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processIntent(intent)

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            pendingMedicamentoFromNotification?.let { medicamento ->
                NavigationManager.setMedicamento(medicamento)
                pendingMedicamentoFromNotification = null
            }
        }, 500)
    }

    private fun processIntent(intent: Intent) {
        when (intent.action) {
            "OPEN_CONFIRMATION" -> {
                val medicamentoJson = intent.getStringExtra("medicamento_json")
                if (medicamentoJson != null) {
                    try {
                        val medicamentoData = Gson().fromJson(medicamentoJson, MedicamentoData::class.java)
                        val medicamento = Medicamento(
                            id = 0,
                            nome = medicamentoData.nome ?: "Não identificado",
                            compostoAtivo = medicamentoData.agente_ativo ?: "Não identificado",
                            dosagem = medicamentoData.dosagem ?: "N/A",
                            quantidade = medicamentoData.quantidade ?: "0",
                            validade = medicamentoData.validade ?: "",
                            horarios = emptyList(),
                            usoContinuo = false,
                            sincronizado = false
                        )
                        pendingMedicamentoFromNotification = medicamento

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun areNotificationsEnabled(): Boolean {
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