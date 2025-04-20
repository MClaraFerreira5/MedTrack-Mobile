package com.example.piec_1.ui.screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.piec_1.R
import com.example.piec_1.model.Medicamento
import com.example.piec_1.ui.components.ErrorDialog
import com.example.piec_1.ui.components.InfoBoxError
import com.example.piec_1.ui.components.InfoBoxSuccess
import com.example.piec_1.ui.components.SuccessDialog
import com.example.piec_1.ui.theme.ErrorColor
import com.example.piec_1.ui.theme.PrimaryColor
import com.example.piec_1.ui.theme.RobotoFont
import com.example.piec_1.ui.theme.SecondaryColor
import com.example.piec_1.viewModel.CameraViewModel
import com.example.piec_1.viewModel.MedicamentoViewModel

@Composable
fun TelaConfirmacao(
    navController: NavController,
    CameraViewModel: CameraViewModel,
    medicamentoViewModel: MedicamentoViewModel
) {
    val medicamentoState = CameraViewModel.medicamento.observeAsState()
    val medicamento = medicamentoState.value
    val MedicamentoDesconhecido = Medicamento(
        id = 0,
        nome = "Desconhecido",
        compostoAtivo = "Desconhecido",
        dosagem = "Desconhecido",
        usoContinuo = false ,
        horarios = listOf<String>()
    )

    val medicamentoEditavel = remember(medicamento) {
        mutableStateOf(medicamento ?: MedicamentoDesconhecido)
    }
    var showEditDialogState = remember { mutableStateOf(false) }
    val showEditDialog = showEditDialogState.value
    val loadingState = remember { mutableStateOf(false) }
    val showSuccessDialog = remember { mutableStateOf(false) }
    val showErrorDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    if (medicamento == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Log.d("Medicamento","$medicamento")

    val success = verificarMedicamento(medicamento!!)
    val message = getMessage(success, medicamento!!)

    if (showEditDialog) {
        Dialog(
            onDismissRequest = { showEditDialogState.value = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Editar Medicamento",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            brush = Brush.horizontalGradient(
                                colors = listOf(PrimaryColor, SecondaryColor)
                            )
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = medicamentoEditavel.value.nome,
                            onValueChange = { medicamentoEditavel.value = medicamentoEditavel.value.copy(nome = it) },
                            label = { Text("Nome") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black.copy(alpha = 0.8f)
                            )
                        )

                        OutlinedTextField(
                            value = medicamentoEditavel.value.compostoAtivo,
                            onValueChange = { medicamentoEditavel.value = medicamentoEditavel.value.copy(compostoAtivo = it) },
                            label = { Text("Composto Ativo") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black.copy(alpha = 0.8f)
                            )
                        )

                        OutlinedTextField(
                            value = medicamentoEditavel.value.dosagem,
                            onValueChange = { medicamentoEditavel.value = medicamentoEditavel.value.copy(dosagem = it) },
                            label = { Text("Dosagem") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { showEditDialogState.value = false },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = PrimaryColor
                            ),
                            border = BorderStroke(1.dp, PrimaryColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                CameraViewModel.atualizarMedicamento(medicamentoEditavel.value)
                                showEditDialogState.value = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = PrimaryColor
                            ),
                            border = BorderStroke(1.dp, PrimaryColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Confirmar")
                        }
                    }
                }
            }
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
            contentAlignment = Alignment.TopCenter
        )
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
                Spacer(modifier = Modifier.height(16.dp))

                if (success) {
                    InfoBoxSuccess(
                        medicamento = medicamento
                    )

                } else {
                    InfoBoxError(message = message)
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (success) {
                    Button(
                        onClick = {
                            loadingState.value = true
                            medicamentoViewModel.confirmarMedicamento(
                                medicamentoCapturado = medicamento,
                                onSuccess = {
                                    loadingState.value = false
                                    showSuccessDialog.value = true
                                },
                                onError = { error ->
                                    loadingState.value = false
                                    errorMessage.value = error
                                    showErrorDialog.value = true
                                }
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor),
                        modifier = Modifier
                            .width(260.dp)
                            .height(50.dp)
                            .padding(top = 0.dp)
                    ) {
                        if (loadingState.value) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text(
                                text = "Confirmar",
                                color = Color.White,
                                fontFamily = RobotoFont,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (showSuccessDialog.value) {
                        SuccessDialog(
                            onDismiss = { showSuccessDialog.value = false },
                            onConfirm = {
                                showSuccessDialog.value = false
                                navController.navigate("TelaPrincipal") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    if (showErrorDialog.value) {
                        ErrorDialog(
                            errorMessage = errorMessage.value,
                            onDismiss = { showErrorDialog.value = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                Button(
                    onClick = { navController.popBackStack() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
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
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showEditDialogState.value = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = PrimaryColor
                    ),
                    border = BorderStroke(1.dp, PrimaryColor),
                    modifier = Modifier
                        .width(260.dp)
                        .height(50.dp)
                        .padding(top = 0.dp)
                ) {
                    Text(
                        text = "Editar",
                        fontFamily = RobotoFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
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