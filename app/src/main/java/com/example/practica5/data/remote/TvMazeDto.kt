package com.example.practica5.data.remote

// Clases para mapear el JSON que viene de TVMaze
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