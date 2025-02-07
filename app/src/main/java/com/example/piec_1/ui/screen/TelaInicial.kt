package com.example.piec_1.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.navigation.NavController
import com.example.piec_1.ui.components.ContentCard
import com.example.piec_1.ui.theme.PrimaryColor
import com.example.piec_1.ui.theme.SecondaryColor

@Composable
fun TelaInicial(navController: NavController) {
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
        ContentCard(navController)
    }
}