package com.example.practica5.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String, // La búsqueda es la clave (para no repetir)
    val timestamp: Long = System.currentTimeMillis() // Para ordenar por el más reciente
)