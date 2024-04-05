package com.example.tarickalia.bd.usuaris

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuarisDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: Usuaris)

    @Query("SELECT * FROM Usuaris WHERE usuari = :username")
    suspend fun findByUsername(username: String): Usuaris?

    @Query("SELECT * FROM Usuaris")
    suspend fun getAll(): List<Usuaris>

    @Query("SELECT * FROM Usuaris WHERE admin = 1")
    suspend fun getAllAdmins(): List<Usuaris>
}