package com.example.piec_1.service

import android.content.Context

class AuthService(context: Context) {
    private val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPref.getString("jwt_token", null)
    }

}