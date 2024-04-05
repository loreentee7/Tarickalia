package com.example.tarickalia.tasques

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.tarickalia.familia.Familia

@Entity(foreignKeys = [ForeignKey(
    entity = Familia::class,
    parentColumns = arrayOf("nombre"),
    childColumns = arrayOf("hijo"),
    onDelete = ForeignKey.CASCADE
)])
data class Tasques(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val hijo: String,
    val nombreTarea: String,
    val dificultad: String,
    val puntuacion: Int,
    val fechaVencimiento: String
)
