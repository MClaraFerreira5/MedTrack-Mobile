package com.example.piec_1.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.piec_1.database.AppDatabase
import com.example.piec_1.model.LoginRequest
import com.example.piec_1.model.Medicamento
import com.example.piec_1.model.Usuario
import com.example.piec_1.service.ApiClient
import com.example.piec_1.sharedPreferences.SharedPreferencesHelper
import kotlinx.coroutines.launch

class LoginViewModel(application: Application): AndroidViewModel(application) {

    private val _loginResponse = MutableLiveData<String>()
    val loginResponse: LiveData<String> get() = _loginResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> get() = _usuario

    private val _medicamentos = MutableLiveData<List<Medicamento>>()
    val medicamentos: LiveData<List<Medicamento>> get() = _medicamentos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val apiService = ApiClient().apiService

    private val database = AppDatabase.getDatabase(application)
    private val usuarioDao = database.usuarioDao()
    private val medicamentoDao = database.medicamentoDao()

    fun login(username: String, password: String, context: Context) {
        _isLoading.postValue(true)
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
                        fetchData(token)
                    }
                } else {
                    _errorMessage.postValue("Usuário ou senha inválidos")
                }
            } catch (e: Exception) {
                Log.e("Login", "Exception: ${e.message}")
                _errorMessage.postValue("Erro ao tentar fazer login. Tente novamente")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun fetchData(token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val usuarioResponse = apiService.getUsuario("Bearer $token")
                if (usuarioResponse.isSuccessful) {
                    val usuario = usuarioResponse.body()
                    if (usuario != null) {
                        usuarioDao.insert(usuario)
                        _usuario.postValue(usuario)
                        Log.d("Room", "Usuário salvo: $usuario")
                    }
                }

                val medicamentosResponse = apiService.getMedicamentos("Bearer $token")
                if (medicamentosResponse.isSuccessful) {
                    val medicamentos = medicamentosResponse.body()
                    if (medicamentos != null) {
                        medicamentoDao.insertAll(medicamentos)
                        _medicamentos.postValue(medicamentos)
                        Log.d("Room", "Medicamentos salvos: $medicamentos")
                    }
                }
            } catch (e: Exception) {
                Log.e("FetchData", "Erro ao buscar dados: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}