package com.example.piec_1.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.piec_1.ui.theme.RobotoFont
import com.example.piec_1.ui.theme.SecondaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntradaDeTexto(
    label: String,
    text: String,
    onTextChange: (String) -> Unit,
    isPassword: Boolean = false,
    isError: Boolean = false
) {
    Text(
        text = label,
        fontSize = 16.sp,
        fontFamily = RobotoFont,
        color = if (isError) Color.Red else Color.Black,
        modifier = Modifier.padding(top = 10.dp)

    )
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation()
        else VisualTransformation.None,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color.White,
            focusedBorderColor = if (isError) Color.Red else SecondaryColor,
            unfocusedBorderColor = if (isError) Color.Red else Color.Gray,
            cursorColor = SecondaryColor,
            focusedLabelColor = if (isError) Color.Red else SecondaryColor,
            unfocusedLabelColor = if (isError) Color.Red else Color(0xFF999999),
            focusedTextColor = Color.Black

        )
    )
}
