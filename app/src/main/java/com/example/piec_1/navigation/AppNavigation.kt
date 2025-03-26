package com.example.piec_1.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.piec_1.ui.screen.TelaLogin
import com.example.piec_1.ui.screen.TelaCamera
import com.example.piec_1.ui.screen.TelaConfirmacao
import com.example.piec_1.ui.screen.TelaEsqueciSenha
import com.example.piec_1.ui.screen.TelaInicial
import com.example.piec_1.ui.screen.TelaPrincipal
import com.example.piec_1.ui.screen.TelaRedefinirSenha
import com.example.piec_1.viewModel.CameraViewModel
import com.example.piec_1.viewModel.LoginViewModel
import com.example.piec_1.viewModelFactory.LoginViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val cameraViewModel: CameraViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(LocalContext.current.applicationContext as Application)
    )


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
            TelaConfirmacao(navController, cameraViewModel)
        }
        composable("TelaCamera"){
            TelaCamera(navController, 0, "", cameraViewModel)
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
                medicamentoId = backStackEntry.arguments?.getLong("medicamentoId") ?: -1,
                horario = backStackEntry.arguments?.getString("horario") ?: "",
                viewModel = cameraViewModel
            )
        }
    }
}