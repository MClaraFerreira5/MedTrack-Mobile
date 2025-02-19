package com.example.piec_1.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.piec_1.model.Medicamento
import com.example.piec_1.ui.components.InfoBox
import com.example.piec_1.ui.components.InfoBoxError
import com.example.piec_1.ui.theme.PrimaryColor
import com.example.piec_1.ui.theme.RobotoFont
import com.example.piec_1.ui.theme.SecondaryColor
import com.example.piec_1.viewModel.CameraViewModel

@Composable
fun TelaConfirmacao(
    navController: NavController,
    viewModel: CameraViewModel
) {
    val medicamento = viewModel.medicamento.observeAsState().value

    if (medicamento == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val success = verificarMedicamento(medicamento!!)
    val message = getMessage(success, medicamento!!)

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
            contentAlignment = Alignment.TopCenter
        )
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
                Spacer(modifier = Modifier.height(16.dp))

                if (success) {
                    InfoBox(
                        message = message,
                        success = success,
                        size = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    InfoBoxError(message = message)
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (success) {
                    Button(
                        onClick = { }, // Salvará no banco de dados SQLite
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor),
                        modifier = Modifier
                            .width(260.dp)
                            .height(50.dp)
                            .padding(top = 0.dp)
                    ) {
                        Text(
                            text = "Confirmar",
                            color = Color.White,
                            fontFamily = RobotoFont,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                } else {
                    Button(
                        onClick = { navController.popBackStack() },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor),
                        modifier = Modifier
                            .width(260.dp)
                            .height(50.dp)
                            .padding(top = 0.dp)
                    ) {
                        Text(
                            text = "Refazer Captura",
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
}

@Composable
fun getMessage(sucess: Boolean, medicamento: Medicamento): String {
    if (sucess) {
        return "Medicamento identificado: ${medicamento.nome}, ${medicamento.compostoAtivo}, ${medicamento.dosagem}"
    }
    return stringResource(id = R.string.confirmacao_falha)
}

private fun verificarMedicamento(medicamento: Medicamento): Boolean {
    return medicamento.nome != "Desconhecido" && medicamento.compostoAtivo != "Desconhecido" &&
            medicamento.dosagem != "Desconhecido"
}

