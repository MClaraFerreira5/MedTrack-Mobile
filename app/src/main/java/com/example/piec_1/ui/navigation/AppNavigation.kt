package com.example.piec_1.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.piec_1.ui.screen.viewModel.LoginViewModelFactory
import com.example.piec_1.utils.connection.ConnectivityObserver

@Composable
fun AppNavigation(
    onNavControllerReady: (NavController) -> Unit = {}
) {
    val navController = rememberNavController()
    val cameraViewModel: CameraViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val context = LocalContext.current

    val connectivityObserver = remember { ConnectivityObserver(context) }

    val medicamentoViewModel: MedicamentoViewModel = viewModel()

    LaunchedEffect(Unit) {
        onNavControllerReady(navController)
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