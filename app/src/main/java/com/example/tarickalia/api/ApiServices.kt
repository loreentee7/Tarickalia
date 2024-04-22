package com.example.tarickalia.api

import com.example.tarickalia.api.Models.Familium
import com.example.tarickalia.api.Models.Recompensa
import com.example.tarickalia.api.Models.Tarea
import com.example.tarickalia.api.Models.Usuario
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {

    // Familiums
    @GET("Familiums")
    suspend fun getFamiliums(): Response<List<Familium>>

    @POST("Familiums")
    suspend fun postFamilium(@Body familium: Familium): Response<Familium>

    @GET("Familiums/{id}")
    suspend fun getFamilium(@Path("id") id: Int): Response<Familium>

    @PUT("Familiums/{id}")
    suspend fun putFamilium(@Path("id") id: Int, @Body familium: Familium): Response<Familium>

    @DELETE("Familiums/{id}")
    suspend fun deleteFamilium(@Path("id") id: Int): Response<Void>

    // Recompensas
    @GET("Recompensas")
    suspend fun getRecompensas(): Response<List<Recompensa>>

    @POST("Recompensas")
    suspend fun postRecompensa(@Body recompensa: Recompensa): Response<Recompensa>

    @GET("Recompensas/{id}")
    suspend fun getRecompensa(@Path("id") id: Int): Response<Recompensa>

    @PUT("Recompensas/{id}")
    suspend fun putRecompensa(@Path("id") id: Int, @Body recompensa: Recompensa): Response<Recompensa>

    @DELETE("Recompensas/{id}")
    suspend fun deleteRecompensa(@Path("id") id: Int): Response<Void>

    // Tareas
    @GET("Tareas")
    suspend fun getTareas(): Response<List<Tarea>>
    
    @POST("Tareas")
    suspend fun createTask(@Body tarea: Tarea): Response<Tarea>

    @GET("Tareas/{id}")
    suspend fun getTarea(@Path("id") id: Int): Response<Tarea>

    @PUT("Tareas/{id}")
    suspend fun putTarea(@Path("id") id: Int, @Body tarea: Tarea): Response<Tarea>

    @DELETE("Tareas/{id}")
    suspend fun deleteTarea(@Path("id") id: Int): Response<Void>

    // Usuarios
    @GET("Usuarios")
    suspend fun getUsuarios(): Response<List<Usuario>>

    @POST("Usuarios")
    suspend fun postUsuario(@Body usuario: Usuario): Response<Usuario>

    @GET("Usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Response<Usuario>

    @PUT("Usuarios/{id}")
    suspend fun putUsuario(@Path("id") id: Int, @Body usuario: Usuario): Response<Usuario>

    @DELETE("Usuarios/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int): Response<Void>

    @GET("Usuarios/Familia/{idFamilia}")
    suspend fun getUsuariosByFamilia(@Path("idFamilia") idFamilia: Int): Response<List<Usuario>>
}