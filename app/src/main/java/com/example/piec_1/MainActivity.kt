package com.example.piec_1
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge


import com.example.piec_1.ui.theme.PIEC1Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PIEC1Theme {
                val interfaceHelper = Interface()
                interfaceHelper.AppNavigation()
            }
        }
    }
}










