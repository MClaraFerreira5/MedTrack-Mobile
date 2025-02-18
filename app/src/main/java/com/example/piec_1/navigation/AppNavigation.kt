package com.example.piec_1.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.piec_1.viewModel.CameraViewModel
import com.example.piec_1.ui.screen.TelaCadastro
import com.example.piec_1.ui.screen.TelaCamera
import com.example.piec_1.ui.screen.TelaEsqueciSenha
import com.example.piec_1.ui.screen.TelaInicial
import com.example.piec_1.ui.screen.TelaPrincipal

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "TelaInicial"
    ) {
        composable("TelaInicial") {
            TelaInicial(navController)
        }
        composable("TelaCadastro") {
            TelaCadastro(navController)
        }
        composable("TelaPrincipal"){
            TelaPrincipal(navController)
        }
        composable("TelaCamera"){
            val viewModel: CameraViewModel = viewModel()
            TelaCamera(navController, viewModel)

        }
        composable("TelaEsqueciSenha"){
            TelaEsqueciSenha(navController)
        }
    }
}