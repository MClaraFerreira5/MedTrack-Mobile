package com.example.piec_1.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.piec_1.ui.screen.TelaCadastro
import com.example.piec_1.ui.screen.TelaCamera
import com.example.piec_1.ui.screen.TelaConfirmacao
import com.example.piec_1.ui.screen.TelaEsqueciSenha
import com.example.piec_1.ui.screen.TelaInicial
import com.example.piec_1.ui.screen.TelaPrincipal
import com.example.piec_1.ui.screen.TelaRedefinirSenha
import com.example.piec_1.viewModel.CameraViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val cameraViewModel: CameraViewModel = viewModel()


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
            TelaCamera(navController, cameraViewModel)
        }
    }
}