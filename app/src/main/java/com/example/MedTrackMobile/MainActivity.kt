package com.example.MedTrackMobile
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


import com.example.MedTrackMobile.ui.theme.PIEC1Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PIEC1Theme {
                val isPermissionGranted = remember { mutableStateOf(false) }

                if (isPermissionGranted.value) {
                    AppNavigation()
                } else {
                    RequestCameraPermission { isGranted ->
                        isPermissionGranted.value = isGranted
                    }
                }
            }
        }
    }

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        val interfaceHelper = Interface()

        NavHost(
            navController = navController,
            startDestination = "TelaInicial"
        ) {
            composable("TelaInicial") {
                interfaceHelper.TelaInicial(navController)
            }
            composable("TelaCadastro") {
                interfaceHelper.TelaCadastro(navController)
            }
            composable("TelaPrincipal"){
                interfaceHelper.TelaPrincipal(navController)
            }
            composable("TelaCamera"){
                val viewModel: CameraViewModel = viewModel()
                interfaceHelper.TelaCamera(navController, viewModel)

            }
        }
    }

    @Composable
    fun RequestCameraPermission(onPermissionResult: (Boolean) -> Unit) {
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
                onPermissionResult(true) // Se já tem permissão, inicia navegação
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}
