package com.example.tarickalia.bd.usuaris

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tarickalia.familia.Converters
import com.example.tarickalia.familia.Familia
import com.example.tarickalia.familia.FamiliaDao

@Database(entities = [Usuaris::class, Familia::class],  version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarisDao(): UsuarisDao
    abstract fun familiaDao(): FamiliaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tarickalia"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}