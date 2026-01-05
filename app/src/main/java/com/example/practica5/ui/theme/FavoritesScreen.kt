package com.example.practica5.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FavoritesScreen(viewModel: MainViewModel) {
    val favoriteShows by viewModel.favorites.collectAsState()
    val recommendedShows by viewModel.recommendations.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp) // Espacio para que no lo tape el bottom bar
    ) {
        // --- SECCIÓN 1: MIS FAVORITOS ---
        item {
            Text(
                text = "Mis Favoritos ❤",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (favoriteShows.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FavoriteBorder, null, modifier = Modifier.size(50.dp))
                        Text("Agrega series para recibir recomendaciones")
                    }
                }
            }
        } else {
            items(favoriteShows) { show ->
                MovieCard(show = show, onFavoriteClick = { viewModel.toggleFavorite(it) })
            }
        }

        // --- SECCIÓN 2: RECOMENDACIONES ---
        if (recommendedShows.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Te podría gustar...",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Basado en los géneros de tus favoritos",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(recommendedShows) { show ->
                // Usamos un color de fondo ligero para distinguir que son recomendaciones
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    MovieCard(show = show, onFavoriteClick = { viewModel.toggleFavorite(it) })
                }
            }
        }
    }
}