package com.example.piec_1.service

import com.example.piec_1.model.LoginRequest
import com.example.piec_1.model.Medicamento
import com.example.piec_1.model.Usuario
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Usuario

    @GET("usuario/{id}")
    suspend fun getUsuario(@Path("id") userId: Long): Usuario

    @GET("medicamento")
    suspend fun getMedicamento(): List<Medicamento>

    @POST("medicamento")
    suspend fun addMedicamento(@Body medicamento: Medicamento): Medicamento

    @DELETE("medicamento/{id}")
    suspend fun deleteMedicamento(@Path("id") id: Long)

}