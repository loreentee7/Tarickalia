package com.example.tarickalia.api.Models

import java.util.Date

data class Usuario(
    var Id: Int? = null,
    var nombreUsuario: String? = null,
    var Correo: String? = null,
    var contraseña: String? = null,
    var Nombre: String? = null,
    var Apellidos: String? = null,
    var FechaNacimiento: Date? = null,
    var admin: Boolean? = null,
    var genero: String? = null,
    var idFamilia: Int? = null,
    var Monedas: Int? = null,
    var idFamiliaNavigation: Familium? = null,
    var tareas: List<Tarea>? = null
)