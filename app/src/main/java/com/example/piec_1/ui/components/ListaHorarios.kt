package com.example.piec_1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.piec_1.R
import com.example.piec_1.ui.theme.RobotoFont

@Composable
fun ListaHorarios(label: String, time: String){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .padding(bottom = 6.dp)
        .background(
            Color.White
        ),
        contentAlignment = Alignment.CenterStart)

    {
        Icon(painter = painterResource(id = R.drawable.img),
            contentDescription = "foto do remédio",
            tint = Color.Unspecified,
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
                .padding(10.dp))

        Text(
            text = "$label - $time",
            fontWeight = FontWeight.Normal,
            fontFamily = RobotoFont,
            fontSize = 32.sp,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)

        )
    }
}
