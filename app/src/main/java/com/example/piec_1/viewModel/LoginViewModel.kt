package com.example.piec_1.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piec_1.model.LoginRequest
import com.example.piec_1.model.Usuario
import com.example.piec_1.service.ApiClient
import com.example.piec_1.sharedPreferences.SharedPreferencesHelper
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginResponse = MutableLiveData<String>()
    val loginResponse: LiveData<String> get() = _loginResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val apiService = ApiClient().apiService

    fun login(username: String, password: String, context: Context) {
        viewModelScope.launch {
            try {
                val response = apiService.login(LoginRequest(username, password))
                Log.d("Login", "Response: $response")
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    if (token != null) {
                        SharedPreferencesHelper.saveToken(context, token)
                        Log.d("Login", "Token: $token")
                        _loginResponse.postValue(token)
                    }
                } else {
                    _errorMessage.postValue("Usuário ou senha inválidos")
                }
            } catch (e: Exception) {
                Log.e("Login", "Exception: ${e.message}")
                _errorMessage.postValue("Erro ao tentar fazer login. Tente novamente")
            }
        }
    }
}