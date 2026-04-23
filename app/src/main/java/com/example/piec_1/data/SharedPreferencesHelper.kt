package com.example.piec_1.data

import android.content.Context
import androidx.core.content.edit

object SharedPreferencesHelper {
    private const val PREFS_NAME = "MyAppPrefs"
    private const val KEY_TOKEN = "jwt_token"

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_TOKEN, token) }
    }

//    fun getToken(context: Context): String? {
//        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        return prefs.getString(KEY_TOKEN, null)
//    }
}