package com.example.practica5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// --- IMPORTS OBLIGATORIOS ---
import com.example.practica5.data.local.AppDatabase
import com.example.practica5.data.remote.FirebaseManager
import com.example.practica5.data.remote.TvMazeApi
import com.example.practica5.data.repository.ShowRepository
import com.example.practica5.ui.FavoritesScreen
import com.example.practica5.ui.HomeScreen
import com.example.practica5.ui.LoginScreen
import com.example.practica5.ui.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de dependencias
        val database = AppDatabase.getDatabase(this)
        val api = TvMazeApi.create()
        val firebaseManager = FirebaseManager()
        val repository = ShowRepository(api, database.showDao(), firebaseManager)

        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MainViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        val viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setContent {
            AppNavigation(viewModel)
        }
    }
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    // Si el usuario ya existe, vamos al Home, si no, al Login
    val startRoute = if (viewModel.isUserLoggedIn()) "home_nav" else "login"

    NavHost(navController = navController, startDestination = startRoute) {
        composable("login") {
            LoginScreen(viewModel = viewModel) {
                // Al loguearse, navegar al Home y borrar el Login del "Back Stack"
                navController.navigate("home_nav") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
        composable("home_nav") {
            MainScreenWithBottomBar(viewModel) {
                // Callback de Logout: salir y volver al login
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo("home_nav") { inclusive = true }
                }
            }
        }
    }
}

// Necesario para usar TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithBottomBar(viewModel: MainViewModel, onLogout: () -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CineVerse") },
                actions = {
                    Button(onClick = onLogout) {
                        Text("Salir")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = currentRoute == "home",
                    onClick = {
                        navController.navigate("home") { popUpTo("home") { inclusive = true } }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
                    label = { Text("Favoritos") },
                    selected = currentRoute == "favorites",
                    onClick = {
                        navController.navigate("favorites") { popUpTo("home") }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(viewModel) }
            composable("favorites") { FavoritesScreen(viewModel) }
        }
    }
}