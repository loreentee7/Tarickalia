package com.example.tarickalia.bd.usuaris

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity
data class Usuaris(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val usuari: String,
    val contrasenya: String,
    val nom: String,
    val cognom: String,
    val admin: Boolean,
    val genre: Boolean,
)
