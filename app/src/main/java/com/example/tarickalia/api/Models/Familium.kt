package com.example.tarickalia.api.Models

data class Familium(
    var id: Int? = null,
    var nombre: String? = null,
    var recompensas: List<Recompensa>? = null,
    var tareas: List<Tarea>? = null,
    var usuarios: List<Usuario>? = null
)


