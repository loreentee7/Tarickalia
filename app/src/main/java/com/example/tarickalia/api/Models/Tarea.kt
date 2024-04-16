package com.example.tarickalia.api.Models

import java.util.Date

data class Tarea(
    var Id: Int? = null,
    var Nombre: String? = null,
    var Dificultad: String? = null,
    var Completada: Boolean? = null,
    var Puntuacion: Int? = null,
    var FechaCaducidad: Date? = null,
    var IdFamilia: Int? = null,
    var IdUsuario: Int? = null,
    var IdFamiliaNavigation: Familium? = null,
    var IdUsuarioNavigation: Usuario? = null
)