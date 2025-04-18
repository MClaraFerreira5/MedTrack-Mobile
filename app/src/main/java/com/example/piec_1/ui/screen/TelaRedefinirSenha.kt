package com.example.piec_1.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.piec_1.R
import com.example.piec_1.ui.components.EntradaDeTexto
import com.example.piec_1.ui.components.InfoBox
import com.example.piec_1.ui.theme.PrimaryColor
import com.example.piec_1.ui.theme.RobotoFont
import com.example.piec_1.ui.theme.SecondaryColor

@Composable
fun TelaRedefinirSenha(navController: NavController) {

    val codigo = remember { mutableStateOf("") }
    val novaSenha = remember { mutableStateOf("") }
    val repetirNovaSenha = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }

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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 100.dp)
            ){
                Text(
                    text = "Redinição de senha",
                    fontFamily = RobotoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.sp
                )
                InfoBox(
                    message = "Um email foi enviado para você com o código para a alteração da senha",
                    success = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(id = R.string.redefinir_senha),
                    fontFamily = RobotoFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 10.dp)

                )
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    EntradaDeTexto(
                        label = "Código",
                        text = codigo.value,
                        onTextChange = { codigo.value = it },
                        isError = errorMessage.value != null
                    )
                    EntradaDeTexto(
                        label = "Nova senha",
                        text = novaSenha.value,
                        onTextChange = { novaSenha.value = it },
                        isPassword = true,
                        isError = errorMessage.value != null
                    )
                    EntradaDeTexto(
                        label = "Repita a nova senha",
                        text = repetirNovaSenha.value,
                        onTextChange = { repetirNovaSenha.value = it },
                        isPassword = true,
                        isError = errorMessage.value != null
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = {
                        if (codigo.value.isBlank() || novaSenha.value.isBlank() ||
                            repetirNovaSenha.value.isBlank()) {
                        errorMessage.value = "Por favor, preencha todos os campos."
                    } else if (novaSenha.value != repetirNovaSenha.value) {
                        errorMessage.value = "As senhas não coincidem."
                    } else {
                        errorMessage.value = null
                        navController.navigate("TelaPrincipal")
                    }},
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor),
                    modifier = Modifier
                        .width(260.dp)
                        .height(50.dp)
                        .padding(top = 0.dp)
                ) {
                    Text(
                        text = "Redefinir",
                        color = Color.White,
                        fontFamily = RobotoFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

}