package com.example.practica5.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TvMazeApi {
    @GET("search/shows")
    suspend fun searchShows(@Query("q") query: String): List<TvMazeResponse>

    companion object {
        private const val BASE_URL = "https://api.tvmaze.com/"

        fun create(): TvMazeApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TvMazeApi::class.java)
        }
    }
}