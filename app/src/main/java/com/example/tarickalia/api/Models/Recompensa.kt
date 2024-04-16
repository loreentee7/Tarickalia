package com.example.tarickalia.api.Models

data class Recompensa(
    var Id: Int? = null,
    var Nombre: String? = null,
    var Puntuacion: Int? = null,
    var Reclamada: Boolean? = null,
    var IdFamilia: Int? = null,
    var IdFamiliaNavigation: Familium? = null
)