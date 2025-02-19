package com.example.piec_1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.piec_1.R
import com.example.piec_1.ui.theme.ButtonColor
import com.example.piec_1.ui.theme.MontserratFont

@Composable
fun ContentCard(navController: NavController) {
    Box(
        modifier = Modifier
            .width(400.dp)
            .height(900.dp)
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(0.dp)
        ) {
            Icon(

                painter = painterResource(id = R.drawable.medtrack_white_icon),
                contentDescription = "Icone Coração",
                tint = Color.Unspecified,
                modifier = Modifier
                    .width(420.dp)
                    .height(400.dp)

            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "MedTrack",
                    fontFamily = MontserratFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp,
                    color = Color.White
                )
            }
            Button(
                onClick = {
                    navController.navigate("TelaCadastro")
                },
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
                modifier = Modifier
                    .padding(top = 50.dp)
                    .width(260.dp)
                    .height(60.dp)
            ) {
                Text(
                    text = "Iniciar",
                    fontSize = 36.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MontserratFont
                )
            }
        }
    }
}
