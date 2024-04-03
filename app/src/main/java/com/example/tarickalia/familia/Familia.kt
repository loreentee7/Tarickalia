package com.example.tarickalia.familia

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Familia(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val nombre: String,
    val admin: String,
    var hijos: List<String>
)
