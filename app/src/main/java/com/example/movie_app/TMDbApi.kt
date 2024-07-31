package com.example.movie_app

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDbApi {
    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Call<UpcomingMoviesResponse>

    @GET("movie/upcoming")
    fun getUpcomingMoviesByRegion(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("region") region: String,
        @Query("page") page: Int
    ): Call<UpcomingMoviesResponse>

    @GET("search/movie")
    fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Call<UpcomingMoviesResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Call<MovieDetailsResponse>

    @GET("discover/movie")
    fun getMoviesByDateRange(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("primary_release_date.gte") startDate: String,
        @Query("primary_release_date.lte") endDate: String,
        @Query("region") region: String,
        @Query("page") page: Int
    ): Call<UpcomingMoviesResponse>
}

data class UpcomingMoviesResponse(
    val results: List<Movie>
)

data class Movie(
    val id: Int,
    val title: String,
    val release_date: String, // YYYY-MM-DD
    val poster_path: String?,
    val overview: String,
    val production_countries: List<Country>?,
    val genre_ids: List<Int> // 장르 ID 리스트 추가
)

data class Country(
    val iso_3166_1: String,
    val name: String
)

data class MovieDetailsResponse(
    val id: Int,
    val title: String,
    val release_date: String,
    val poster_path: String?,
    val overview: String,
    val runtime: Int,
    val genres: List<Genre>
)

data class Genre(
    val id: Int,
    val name: String
)
