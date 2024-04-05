package com.example.tarickalia.familia

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FamiliaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(familia: Familia)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun create(familia: Familia)

    @Query("SELECT * FROM Familia WHERE nombre = :nombre")
    suspend fun findByName(nombre: String): Familia?

    @Query("SELECT * FROM Familia")
    suspend fun getAll(): List<Familia>

    @Update
    suspend fun update(familia: Familia)
}