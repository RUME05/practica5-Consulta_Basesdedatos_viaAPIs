package com.example.practica5.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shows")
data class ShowEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val summary: String?,
    val imageUrl: String?,
    val rating: Double?,
    val genres: String,
    var isFavorite: Boolean = false
)