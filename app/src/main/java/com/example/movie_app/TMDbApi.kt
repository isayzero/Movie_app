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
    ): Call<UpcomingMoviesResponse> // 다가오는 영화를 가져오는 API 호출이다.

    @GET("movie/upcoming")
    fun getUpcomingMoviesByRegion(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("region") region: String,
        @Query("page") page: Int
    ): Call<UpcomingMoviesResponse> // 지역별 다가오는 영화를 가져오는 API 호출이다.

    @GET("search/movie")
    fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Call<UpcomingMoviesResponse> // 영화를 검색하는 API 호출이다.

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Call<MovieDetailsResponse> // 특정 영화의 세부 정보를 가져오는 API 호출이다.

    @GET("discover/movie")
    fun getMoviesByDateRange(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("primary_release_date.gte") startDate: String,
        @Query("primary_release_date.lte") endDate: String,
        @Query("region") region: String,
        @Query("page") page: Int
    ): Call<UpcomingMoviesResponse> // 특정 날짜 범위 내에서 영화를 가져오는 API 호출이다.
}

data class UpcomingMoviesResponse(
    val results: List<Movie> // 영화 목록을 나타낸다.
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
    val genres: List<Genre> // 영화 장르 리스트이다.
)

data class Genre(
    val id: Int,
    val name: String
)
