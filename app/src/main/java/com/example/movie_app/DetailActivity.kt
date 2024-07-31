package com.example.movie_app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var tmdbApi: TMDbApi
    private val API_KEY = "4dbe136f0b014f494c23256f91bf9545"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val movieId = intent.getIntExtra("movie_id", -1)
        if (movieId != -1) {
            setupRetrofit()
            fetchMovieDetails(movieId)
        } else {
            Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        tmdbApi = retrofit.create(TMDbApi::class.java)
    }

    private fun fetchMovieDetails(movieId: Int) {
        tmdbApi.getMovieDetails(movieId, API_KEY, "ko-KR").enqueue(object : Callback<MovieDetailsResponse> {
            override fun onResponse(call: Call<MovieDetailsResponse>, response: Response<MovieDetailsResponse>) {
                if (response.isSuccessful) {
                    val movieDetails = response.body()
                    movieDetails?.let { displayMovieDetails(it) }
                } else {
                    Toast.makeText(this@DetailActivity, "API call failed: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MovieDetailsResponse>, t: Throwable) {
                Toast.makeText(this@DetailActivity, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun displayMovieDetails(movie: MovieDetailsResponse) {
        val movieTitle = findViewById<TextView>(R.id.movieTitle)
        val movieOverview = findViewById<TextView>(R.id.movieOverview)
        val moviePoster = findViewById<ImageView>(R.id.moviePoster)
        val movieReleaseDate = findViewById<TextView>(R.id.movieReleaseDate)
        val movieRuntime = findViewById<TextView>(R.id.movieRuntime)
        val movieGenres = findViewById<TextView>(R.id.movieGenres)

        movieTitle.text = movie.title
        movieOverview.text = movie.overview
        movieReleaseDate.text = "개봉일: ${movie.release_date}"
        movieRuntime.text = "상영시간: ${movie.runtime} 분"
        movieGenres.text = "장르: ${movie.genres.joinToString(", ") { it.name }}"

        Picasso.get().load("https://image.tmdb.org/t/p/w500${movie.poster_path}").into(moviePoster)
    }
}
