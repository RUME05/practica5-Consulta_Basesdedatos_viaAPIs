import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// 1. Entidad para la Base de Datos (Lo que guardamos en el celular)
@Entity(tableName = "shows")
data class ShowEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val summary: String?, // HTML description
    val imageUrl: String?,
    val rating: Double?,
    val genres: String, // Guardaremos la lista como "Drama,Action" (String simple)
    var isFavorite: Boolean = false // Preparado para Ejercicio 3
)

// 2. Modelos para recibir respuesta de TVMaze (API)
// La API devuelve una lista de objetos que contienen "show"
data class TvMazeResponse(
    val show: TvMazeDto
)

data class TvMazeDto(
    val id: Int,
    val name: String,
    val summary: String?,
    val image: ImageDto?,
    val rating: RatingDto?,
    val genres: List<String>
)

data class ImageDto(val medium: String?, val original: String?)
data class RatingDto(val average: Double?)