package com.example.piec_1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.piec_1.domain.model.Medicamento
import com.example.piec_1.ui.screen.TelaLogin
import com.example.piec_1.ui.screen.TelaCamera
import com.example.piec_1.ui.screen.TelaConfirmacao
import com.example.piec_1.ui.screen.TelaEsqueciSenha
import com.example.piec_1.ui.screen.TelaInicial
import com.example.piec_1.ui.screen.TelaPrincipal
import com.example.piec_1.ui.screen.TelaRedefinirSenha
import com.example.piec_1.ui.screen.viewModel.CameraViewModel
import com.example.piec_1.ui.screen.viewModel.LoginViewModel
import com.example.piec_1.ui.screen.viewModel.MedicamentoViewModel
import com.example.piec_1.utils.connection.ConnectivityObserver
import kotlinx.coroutines.delay

@Composable
fun AppNavigation(
    onNavControllerReady: (NavController) -> Unit = {}
) {
    val navController = rememberNavController()
    val cameraViewModel: CameraViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val context = LocalContext.current
    val connectivityObserver = remember { ConnectivityObserver(context) }
    val medicamentoViewModel: MedicamentoViewModel = hiltViewModel()

    val shouldNavigate = NavigationManager.shouldNavigate.collectAsState()

    LaunchedEffect(Unit) {
        onNavControllerReady(navController)
    }

    LaunchedEffect(shouldNavigate.value) {
        if (shouldNavigate.value != null) {
            delay(300) // Pequeno delay para garantir que a UI está pronta
            val medicamento = shouldNavigate.value
            cameraViewModel.atualizarMedicamento(medicamento!!)
            navController.navigate("TelaConfirmacao") {
                popUpTo("TelaInicial") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "TelaInicial"
    ) {
        composable("TelaInicial") {
            TelaInicial(navController)
        }
        composable("TelaLogin") {
            TelaLogin(navController, loginViewModel)
        }
        composable("TelaPrincipal"){
            TelaPrincipal(navController, loginViewModel)
        }
        composable("TelaEsqueciSenha"){
            TelaEsqueciSenha(navController)
        }
        composable("TelaRedefinirSenha"){
            TelaRedefinirSenha(navController)
        }
        composable("TelaConfirmacao"){
            TelaConfirmacao(navController, cameraViewModel, medicamentoViewModel)
        }
        composable("TelaCamera"){
            TelaCamera(navController, cameraViewModel, connectivityObserver)
        }
        composable(
            "TelaCamera/{medicamentoId}/{horario}",
            arguments = listOf(
                navArgument("medicamentoId") { type = NavType.LongType },
                navArgument("horario") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            TelaCamera(
                navController = navController,
                viewModel = cameraViewModel,
                connectivityObserver = connectivityObserver
            )
        }
    }
}
