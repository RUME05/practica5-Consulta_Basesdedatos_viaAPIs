package com.example.practica5.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.practica5.data.local.ShowEntity

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val shows by viewModel.shows.collectAsState()
    val history by viewModel.searchHistory.collectAsState()
    var textSearch by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Barra de Búsqueda
        OutlinedTextField(
            value = textSearch,
            onValueChange = {
                textSearch = it
                viewModel.buscarSerie(it)
            },
            label = { Text("Buscar series (Ej: Flash)") },
            modifier = Modifier.fillMaxWidth()
        )

        // --- HISTORIAL DE BÚSQUEDA (Burbujas) ---
        if (history.isNotEmpty() && textSearch.isEmpty()) {
            Text(
                text = "Recientes:",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                items(history) { item ->
                    SuggestionChip(
                        onClick = {
                            textSearch = item.query
                            viewModel.buscarSerie(item.query)
                        },
                        label = { Text(item.query) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }
        // ----------------------------------------

        Spacer(modifier = Modifier.height(10.dp))

        // Lista de Resultados
        LazyColumn {
            items(shows) { show ->
                MovieCard(show = show, onFavoriteClick = { viewModel.toggleFavorite(it) })
            }
        }
    }
}

@Composable
fun MovieCard(show: ShowEntity, onFavoriteClick: (ShowEntity) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = show.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = show.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Rating: ${show.rating ?: "N/A"}")
                Text(text = show.genres, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { onFavoriteClick(show) }) {
                Icon(
                    imageVector = if (show.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (show.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}