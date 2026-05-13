package com.example.piec_1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.piec_1.ui.screen.TelaCamera
import com.example.piec_1.ui.screen.TelaConfirmacao
import com.example.piec_1.ui.screen.TelaEsqueciSenha
import com.example.piec_1.ui.screen.TelaInicial
import com.example.piec_1.ui.screen.TelaLogin
import com.example.piec_1.ui.screen.TelaPrincipal
import com.example.piec_1.ui.screen.TelaRedefinirSenha
import com.example.piec_1.ui.screen.viewModel.CameraViewModel
import com.example.piec_1.ui.screen.viewModel.LoginViewModel
import com.example.piec_1.ui.screen.viewModel.MedicamentoViewModel
import com.example.piec_1.utils.connection.ConnectivityObserver
import kotlinx.coroutines.delay

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val cameraViewModel: CameraViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val medicamentoViewModel: MedicamentoViewModel = hiltViewModel()
    val context = LocalContext.current
    val connectivityObserver = remember { ConnectivityObserver(context) }
    val shouldNavigate = NavigationManager.shouldNavigate.collectAsState()
    val shouldNavigateFromCamera by cameraViewModel.navigateToConfirmation.observeAsState(false)

    LaunchedEffect(shouldNavigate.value) {
        shouldNavigate.value?.let { medicamento ->
            delay(300)
            cameraViewModel.atualizarMedicamento(medicamento)
            navController.navigate("TelaConfirmacao") {
                popUpTo("TelaInicial") { inclusive = true }
                launchSingleTop = true
            }
            NavigationManager.reset()
        }
    }

    LaunchedEffect(shouldNavigateFromCamera) {
        if (shouldNavigateFromCamera) {
            navController.navigate("TelaConfirmacao")
            cameraViewModel.onNavigationToConfirmationHandled()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "TelaInicial"
    ) {
        composable("TelaInicial") {
            TelaInicial(
                onStartClick = { navController.navigate("TelaLogin") }
            )
        }
        composable("TelaLogin") {
            TelaLogin(
                loginViewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate("TelaPrincipal") {
                        popUpTo("TelaLogin") { inclusive = true }
                    }
                },
                onForgotPasswordClick = { navController.navigate("TelaEsqueciSenha") }
            )
        }
        composable("TelaPrincipal") {
            TelaPrincipal(
                loginViewModel = loginViewModel,
                onScanClick = { navController.navigate("TelaCamera") }
            )
        }
        composable("TelaEsqueciSenha") {
            TelaEsqueciSenha(
                onEmailSent = { navController.navigate("TelaRedefinirSenha") },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable("TelaRedefinirSenha") {
            TelaRedefinirSenha(
                onPasswordReset = {
                    navController.navigate("TelaPrincipal") {
                        popUpTo("TelaLogin") { inclusive = true }
                    }
                }
            )
        }
        composable("TelaConfirmacao") {
            TelaConfirmacao(
                cameraViewModel = cameraViewModel,
                medicamentoViewModel = medicamentoViewModel,
                onConfirmSuccess = {
                    navController.navigate("TelaPrincipal") {
                        popUpTo("TelaPrincipal") { inclusive = true }
                    }
                },
                onRetakePhoto = { navController.popBackStack() }
            )
        }
        composable("TelaCamera") {
            TelaCamera(
                onBackClick = { navController.popBackStack() },
                viewModel = cameraViewModel,
                connectivityObserver = connectivityObserver
            )
        }
        composable(
            "TelaCamera/{medicamentoId}/{horario}",
            arguments = listOf(
                navArgument("medicamentoId") { type = NavType.LongType },
                navArgument("horario") { type = NavType.StringType }
            )
        ) {
            TelaCamera(
                onBackClick = { navController.popBackStack() },
                viewModel = cameraViewModel,
                connectivityObserver = connectivityObserver
            )
        }
    }
}
