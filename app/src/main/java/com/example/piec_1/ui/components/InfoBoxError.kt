package com.example.piec_1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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

@Composable
fun InfoBoxError(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFFFE0E0), RoundedCornerShape(8.dp))
            .border(2.dp, Color.Red, RoundedCornerShape(8.dp))
            .padding(24.dp) // Aumenta a área interna
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Atenção",
                tint = Color.Red,
                modifier = Modifier.size(40.dp) // Ícone maior
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge, // Fonte maior
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Dicas para melhorar a leitura:",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp, // Aumenta a fonte manualmente
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                Text("✔ Posicione a câmera de frente para o texto", fontSize = 16.sp)
                Text("✔ Evite reflexos e sombras", fontSize = 16.sp)
                Text("✔ Experimente um ambiente mais iluminado", fontSize = 16.sp)
            }
        }
    }
}
