package com.example.piec_1.data.remote

import com.example.piec_1.domain.model.DadosConfirmacaoRequest
import com.example.piec_1.domain.model.LoginRequest
import com.example.piec_1.domain.model.Medicamento
import com.example.piec_1.domain.model.Usuario
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    @Multipart
    @POST("http://192.168.1.107:8000/detect")
    suspend fun scanMedicamento(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<ScanResponse>

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


data class ScanResponse(
    val status: String,
    val data: MedicamentoData, // Os campos estão aqui dentro!
    val count: Int
)


data class MedicamentoData(
    val nome: String?,
    val agente_ativo: String?,
    val dosagem: String?,
    val quantidade: String?,
    val validade: String? = null
)