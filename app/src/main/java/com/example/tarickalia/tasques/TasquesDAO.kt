package com.example.tarickalia.tasques

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TasquesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tasques: Tasques)

    @Query("SELECT * FROM Tasques WHERE hijo = :hijo")
    suspend fun findByHijo(hijo: String): List<Tasques>

    @Query("SELECT * FROM Tasques")
    suspend fun getAll(): List<Tasques>

    @Update
    suspend fun update(tasques: Tasques)
}