package com.example.practica5.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShowDao {
    @Query("SELECT * FROM shows")
    fun getAllShows(): Flow<List<ShowEntity>>

    @Query("SELECT * FROM shows WHERE name LIKE '%' || :query || '%'")
    fun searchShowsLocal(query: String): Flow<List<ShowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShows(shows: List<ShowEntity>)

    @Query("SELECT * FROM shows WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<ShowEntity>>

    @Query("UPDATE shows SET isFavorite = :esFavorito WHERE id = :id")
    suspend fun updateFavorite(id: Int, esFavorito: Boolean)

    // ESTA ES LA FUNCIÓN QUE FALTABA PARA LA SINCRONIZACIÓN
    @Query("SELECT id FROM shows WHERE isFavorite = 1")
    suspend fun getFavoriteIds(): List<Int>

    // HISTORIAL
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: SearchHistoryEntity)

    @Query("SELECT * FROM history ORDER BY timestamp DESC LIMIT 10")
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Query("DELETE FROM shows")
    suspend fun clearAllShows()

    // Borrar el historial (Para que María no vea lo que buscó Juan)
    @Query("DELETE FROM history")
    suspend fun clearHistory()
}