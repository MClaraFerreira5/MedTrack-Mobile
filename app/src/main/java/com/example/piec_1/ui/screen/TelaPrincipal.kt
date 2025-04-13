package com.example.piec_1.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.piec_1.R
import com.example.piec_1.ui.components.ListaHorarios
import com.example.piec_1.ui.theme.ButtonCamera
import com.example.piec_1.ui.theme.PrimaryColor
import com.example.piec_1.ui.theme.SecondaryColor
import com.example.piec_1.viewModel.LoginViewModel

@Composable
fun TelaPrincipal(navController: NavController, loginViewModel: LoginViewModel) {

    val usuario = loginViewModel.usuario.observeAsState().value
    val medicamentos = loginViewModel.medicamentos.observeAsState().value
    val isLoading = usuario == null || medicamentos == null

    Log.d("Principal", "Carregamento: $isLoading")
    Log.d("Principal", "Usuário: $usuario")
    Log.d("Principal", "Medicamentos: $medicamentos")

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = listOf(PrimaryColor, SecondaryColor))
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(750.dp)
                .background(
                    color = Color.White,
                )
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .align(Alignment.TopStart)
                    .background(
                        color = PrimaryColor,
                        shape = CircleShape
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.medtrack_white_icon),
                    contentDescription = "Ícone MedTrack",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.user_icon),
                contentDescription = "Perfil",
                tint = Color.Unspecified,
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .align(Alignment.TopEnd)
            )

            Button(
                onClick = { navController.navigate("TelaCamera") },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "imagem de uma câmera",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp)
                )
            }

            Box(
                modifier = Modifier
                    .width(363.dp)
                    .height(590.dp)
                    .padding(top = 60.dp)
                    .background(ButtonCamera)
                    .align(Alignment.TopStart)
                    .verticalScroll(rememberScrollState())
            ) {
                ListaHorarios(medicamentos)
            }
        }
    }
}