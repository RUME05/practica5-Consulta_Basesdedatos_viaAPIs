package com.example.practica5.data.repository

import com.example.practica5.data.local.SearchHistoryEntity
import com.example.practica5.data.local.ShowDao
import com.example.practica5.data.local.ShowEntity
import com.example.practica5.data.remote.FirebaseManager // <--- IMPORTANTE
import com.example.practica5.data.remote.TvMazeApi
import kotlinx.coroutines.flow.Flow

class ShowRepository(
    private val api: TvMazeApi,
    private val dao: ShowDao,
    private val firebase: FirebaseManager // Ahora sí reconocerá el tipo
) {

    // --- FLUJOS ---
    val allShows: Flow<List<ShowEntity>> = dao.getAllShows()
    val favoriteShows: Flow<List<ShowEntity>> = dao.getFavorites()
    val searchHistory: Flow<List<SearchHistoryEntity>> = dao.getSearchHistory()

    // --- FUNCIONES LOCALES ---
    fun searchLocal(query: String): Flow<List<ShowEntity>> = dao.searchShowsLocal(query)

    suspend fun saveToHistory(query: String) = dao.insertHistory(SearchHistoryEntity(query = query))

    // --- FUNCIONES FIREBASE (AUTH) ---
    fun isUserLoggedIn(): Boolean = firebase.isUserLoggedIn()

    suspend fun login(email: String, pass: String) {
        // 1. Limpiamos datos del usuario anterior (o datos basura viejos)
        dao.clearAllShows()
        dao.clearHistory()

        // 2. Login en Firebase
        firebase.login(email, pass)

        // 3. Descargar datos limpios del nuevo usuario
        syncCloudData()
    }

    suspend fun register(email: String, pass: String) {
        firebase.register(email, pass)
    }

    suspend fun logout() {
        firebase.logout()
        // Al salir, borramos todo localmente para proteger la privacidad
        dao.clearAllShows()
        dao.clearHistory()
    }

    // --- SINCRONIZACIÓN ---
    private suspend fun syncCloudData() {
        try {
            val cloudFavorites = firebase.getFavoritesFromCloud()
            if (cloudFavorites.isNotEmpty()) {
                // Usamos una estrategia segura: insertar pero mantener IDs si es necesario
                dao.insertShows(cloudFavorites)
                // Aseguramos que se marquen como favoritos localmente
                cloudFavorites.forEach { dao.updateFavorite(it.id, true) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun toggleFavorite(show: ShowEntity) {
        val nuevoEstado = !show.isFavorite
        // 1. Local
        dao.updateFavorite(show.id, nuevoEstado)
        // 2. Nube
        try {
            if (nuevoEstado) firebase.saveFavoriteToCloud(show)
            else firebase.removeFavoriteFromCloud(show.id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun searchAndSync(query: String) {
        try {
            val response = api.searchShows(query)
            val currentFavoriteIds = dao.getFavoriteIds()
            val entities = response.map { item ->
                ShowEntity(
                    id = item.show.id,
                    name = item.show.name,
                    summary = item.show.summary,
                    imageUrl = item.show.image?.medium,
                    rating = item.show.rating?.average,
                    genres = item.show.genres.joinToString(","),
                    isFavorite = item.show.id in currentFavoriteIds
                )
            }
            dao.insertShows(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}