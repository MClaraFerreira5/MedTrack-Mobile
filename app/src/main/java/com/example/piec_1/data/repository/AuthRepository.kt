package com.example.piec_1.data.repository

import android.content.Context
import com.example.piec_1.data.PreferencesManager
import com.example.piec_1.data.remote.ApiService
import com.example.piec_1.domain.model.LoginRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val apiService: ApiService
) {
    suspend fun login(username: String, password: String): String = withContext(Dispatchers.IO) {
        val response = apiService.login(LoginRequest(username, password))

        if (!response.isSuccessful) {
            throw LoginException("Usuario ou senha invalidos")
        }

        val token = response.body()?.token
            ?: throw LoginException("Token invalido")

        PreferencesManager.saveToken(context, token)
        token
    }

    fun getToken(): String? = PreferencesManager.getToken(context)
}

class LoginException(message: String) : Exception(message)
