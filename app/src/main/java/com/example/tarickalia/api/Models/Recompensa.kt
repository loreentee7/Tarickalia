package com.example.tarickalia.api.Models

data class Recompensa(
    var id: Int? = null,
    var nombre: String? = null,
    var puntuacion: Int? = null,
    var reclamada: Boolean? = null,
    var idFamilia: Int? = null,
    var idFamiliaNavigation: Familium? = null
)