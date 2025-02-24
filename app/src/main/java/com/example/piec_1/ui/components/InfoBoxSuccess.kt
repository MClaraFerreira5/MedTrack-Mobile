package com.example.piec_1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.piec_1.model.Medicamento

@Composable
fun InfoBoxSuccess(
    medicamento: Medicamento,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFE0FFE0), RoundedCornerShape(8.dp))
            .border(2.dp, Color.Green, RoundedCornerShape(8.dp))
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Sucesso",
                tint = Color.Green,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Medicamento identificado!",
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text("Nome: ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(medicamento.nome, fontSize = 18.sp)
                }
                Row {
                    Text("Composto Ativo: ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(medicamento.compostoAtivo, fontSize = 18.sp)
                }
                Row {
                    Text("Dosagem: ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(medicamento.dosagem, fontSize = 18.sp)
                }
            }
        }
    }
}