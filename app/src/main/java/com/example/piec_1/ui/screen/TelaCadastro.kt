package com.example.piec_1.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.piec_1.R
import com.example.piec_1.ui.components.EntradaDeTexto
import com.example.piec_1.ui.theme.PrimaryColor
import com.example.piec_1.ui.theme.RobotoFont
import com.example.piec_1.ui.theme.SecondaryColor
import com.example.piec_1.viewModel.LoginViewModel

@Composable
fun TelaCadastro(navController: NavController, loginViewModel: LoginViewModel) {

    val loginResponse = loginViewModel.loginResponse.observeAsState().value
    val errorMessage = loginViewModel.errorMessage.observeAsState().value

    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val context = LocalContext.current

    val onLoginClick = {
        Log.d("Login", "Username: ${username.value}, Password: ${password.value}")
        loginViewModel.login(username.value, password.value, context)
    }

    val isError = errorMessage != null

    LaunchedEffect(loginResponse) {
        if (loginResponse != null) {
            navController.navigate("TelaPrincipal")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryColor, SecondaryColor)
                )
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
                .padding(18.dp),
            contentAlignment = Alignment.TopCenter)


        {
            Icon(
                painter = painterResource(id = R.drawable.medtrack_green_icon),
                contentDescription = "Icone Coração",
                tint = Color.Unspecified,
                modifier = Modifier
                    .width(47.dp)
                    .height(47.dp)
                    .align(Alignment.TopStart)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 100.dp)
            ){
                Text(
                    text = "Entrar",
                    fontFamily = RobotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 46.sp
                )
                Text(
                    text = "Preencha os campos abaixo.",
                    fontFamily = RobotoFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 10.dp)

                )
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    EntradaDeTexto(
                        label = "Usuário",
                        text = username.value,
                        onTextChange = { username.value = it },
                        isError = isError
                        )
                    EntradaDeTexto(
                        label = "Senha",
                        text = password.value,
                        onTextChange = { password.value = it },
                        isPassword = true,
                        isError = isError
                    )

                }
                Spacer(modifier = Modifier.height(40.dp))

                errorMessage?.let {
                    Text(text = it, color = Color.Red, fontSize = 14.sp)
                }

                Button(
                    onClick = { onLoginClick() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor),
                    modifier = Modifier
                        .width(260.dp)
                        .height(50.dp)
                        .padding(top = 0.dp)
                ) {
                    Text(
                        text = "Entrar",
                        color = Color.White,
                        fontFamily = RobotoFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(onClick = {navController.navigate("TelaEsqueciSenha")}) {

                    Text(
                        modifier = Modifier.padding(top = 0.dp),
                        text = "Esqueceu sua senha?",
                        fontSize = 14.sp,
                        fontFamily = RobotoFont,

                        )
                }
            }
        }
    }
}