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
            navController.navigate(AppRoutes.CONFIRMACAO) {
                popUpTo(AppRoutes.INICIAL) { inclusive = true }
                launchSingleTop = true
            }
            NavigationManager.reset()
        }
    }

    LaunchedEffect(shouldNavigateFromCamera) {
        if (shouldNavigateFromCamera) {
            navController.navigate(AppRoutes.CONFIRMACAO)
            cameraViewModel.onNavigationToConfirmationHandled()
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.INICIAL
    ) {
        composable(AppRoutes.INICIAL) {
            TelaInicial(
                onStartClick = { navController.navigate(AppRoutes.LOGIN) }
            )
        }
        composable(AppRoutes.LOGIN) {
            TelaLogin(
                loginViewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(AppRoutes.PRINCIPAL) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { navController.navigate(AppRoutes.ESQUECI_SENHA) }
            )
        }
        composable(AppRoutes.PRINCIPAL) {
            TelaPrincipal(
                loginViewModel = loginViewModel,
                onScanClick = { navController.navigate(AppRoutes.CAMERA) }
            )
        }
        composable(AppRoutes.ESQUECI_SENHA) {
            TelaEsqueciSenha(
                onEmailSent = { navController.navigate(AppRoutes.REDEFINIR_SENHA) },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.REDEFINIR_SENHA) {
            TelaRedefinirSenha(
                onPasswordReset = {
                    navController.navigate(AppRoutes.PRINCIPAL) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoutes.CONFIRMACAO) {
            TelaConfirmacao(
                cameraViewModel = cameraViewModel,
                medicamentoViewModel = medicamentoViewModel,
                onConfirmSuccess = {
                    navController.navigate(AppRoutes.PRINCIPAL) {
                        popUpTo(AppRoutes.PRINCIPAL) { inclusive = true }
                    }
                },
                onRetakePhoto = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.CAMERA) {
            TelaCamera(
                onBackClick = { navController.popBackStack() },
                viewModel = cameraViewModel,
                connectivityObserver = connectivityObserver
            )
        }
        composable(
            AppRoutes.CAMERA_FROM_NOTIFICATION,
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
