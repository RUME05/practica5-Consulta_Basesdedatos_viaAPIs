package com.example.practica5.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica5.data.local.ShowEntity
import com.example.practica5.data.repository.ShowRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(private val repository: ShowRepository) : ViewModel() {

    // --- BÚSQUEDA ---
    private val _searchQuery = MutableStateFlow("")

    val shows = _searchQuery.flatMapLatest { query ->
        if (query.isEmpty()) repository.allShows
        else repository.searchLocal(query)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- FAVORITOS ---
    val favorites = repository.favoriteShows.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- HISTORIAL ---
    val searchHistory = repository.searchHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- RECOMENDACIONES ---
    val recommendations = combine(repository.allShows, favorites) { all, favs ->
        if (favs.isEmpty()) {
            emptyList()
        } else {
            val userGenres = favs.flatMap { it.genres.split(",") }.toSet()
            all.filter { show ->
                !show.isFavorite && show.genres.split(",").any { it in userGenres }
            }.take(5)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- FUNCIONES ---

    fun buscarSerie(query: String) {
        _searchQuery.value = query
        if (query.length > 2) {
            viewModelScope.launch {
                repository.saveToHistory(query)
                repository.searchAndSync(query)
            }
        }
    }

    fun toggleFavorite(show: ShowEntity) {
        viewModelScope.launch {
            // CORRECCIÓN AQUÍ: Pasamos el objeto completo 'show', no el ID
            repository.toggleFavorite(show)
        }
    }

    // --- AUTH ---
    suspend fun login(email: String, pass: String): Boolean {
        return try {
            repository.login(email, pass)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun register(email: String, pass: String): Boolean {
        return try {
            repository.register(email, pass)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isUserLoggedIn() = repository.isUserLoggedIn()
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}