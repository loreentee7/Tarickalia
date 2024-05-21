package com.example.tarickalia.api.Models

import java.util.Date

data class Tarea(
    var id: Int? = null,
    var nombre: String? = null,
    var dificultad: String? = null,
    var completada: Boolean? = null,
    var aprobada: Boolean? = null,
    var puntuacion: Int? = null,
    var fechaCaducidad: String? = null,
    var idFamilia: Int? = null,
    var idUsuario: Int? = null,
    var idFamiliaNavigation: Familium? = null,
    var idUsuarioNavigation: Usuario? = null
)