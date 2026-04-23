package com.example.piec_1.data.remote

import com.example.piec_1.domain.model.DadosConfirmacaoRequest
import com.example.piec_1.domain.model.LoginRequest
import com.example.piec_1.domain.model.Medicamento
import com.example.piec_1.domain.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("auth/mobile/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("usuario/mobile")
    suspend fun getUsuario(@Header("Authorization") token: String): Response<Usuario>

    @GET("medicamento/mobile/lista")
    suspend fun getMedicamentos(@Header("Authorization") token: String): Response<List<Medicamento>>

    @POST("/api/confirmacao")
    suspend fun confirmarMedicamento(
        @Header("Authorization") token: String,
        @Body request: DadosConfirmacaoRequest
    ): Response<ConfirmacaoResponse>

}

data class LoginResponse(
    val token: String
)

data class ConfirmacaoResponse(
    val id: Long,
    val medicamentoId: Long,
    val usuarioId: Long,
    val horario: String,
    val data: String,
    val foiTomado: Boolean,
    val observacao: String?,
    val mensagem: String? = null
)