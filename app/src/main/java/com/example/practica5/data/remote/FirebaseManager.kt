package com.example.practica5.data.remote

import com.example.practica5.data.local.ShowEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // --- AUTENTICACIÓN ---
    fun isUserLoggedIn(): Boolean = auth.currentUser != null
    fun getUserId(): String? = auth.currentUser?.uid

    suspend fun login(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).await()
    }

    suspend fun register(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass).await()
    }

    fun logout() {
        auth.signOut()
    }

    // --- FIRESTORE (NUBE) ---
    suspend fun saveFavoriteToCloud(show: ShowEntity) {
        val userId = getUserId() ?: return

        val showData = hashMapOf(
            "id" to show.id,
            "name" to show.name,
            "imageUrl" to show.imageUrl,
            "genres" to show.genres,
            "rating" to show.rating
        )

        db.collection("users").document(userId)
            .collection("favorites").document(show.id.toString())
            .set(showData)
            .await()
    }

    suspend fun removeFavoriteFromCloud(showId: Int) {
        val userId = getUserId() ?: return
        db.collection("users").document(userId)
            .collection("favorites").document(showId.toString())
            .delete()
            .await()
    }

    suspend fun getFavoritesFromCloud(): List<ShowEntity> {
        val userId = getUserId() ?: return emptyList()

        try {
            val snapshot = db.collection("users").document(userId)
                .collection("favorites").get().await()

            return snapshot.documents.mapNotNull { doc ->
                ShowEntity(
                    id = doc.getLong("id")?.toInt() ?: 0,
                    name = doc.getString("name") ?: "",
                    summary = null,
                    imageUrl = doc.getString("imageUrl"),
                    rating = doc.getDouble("rating"),
                    genres = doc.getString("genres") ?: "",
                    isFavorite = true
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
}