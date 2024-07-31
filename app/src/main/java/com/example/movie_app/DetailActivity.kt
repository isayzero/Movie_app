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
    private val API_KEY = "4dbe136f0b014f494c23256f91bf9545"//TMDB에서 발급받은 API 키

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val movieId = intent.getIntExtra("movie_id", -1)// Intent로부터 movie_id를 가져온다.
        if (movieId != -1) {
            setupRetrofit()// Retrofit 설정을 초기화한다.
            fetchMovieDetails(movieId)// 영화 상세 정보를 가져오는 메서드를 호출한다.
        } else {
            Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_SHORT).show()
            finish()// movie_id가 유효하지 않으면 액티비티를 종료한다.
        }
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")// TMDb API의 기본 URL을 설정한다.
            .addConverterFactory(GsonConverterFactory.create())// JSON 변환을 위해 GsonConverterFactory를 추가한다.
            .build()

        tmdbApi = retrofit.create(TMDbApi::class.java)// TMDbApi 인터페이스를 구현한 객체를 생성한다.
    }

    private fun fetchMovieDetails(movieId: Int) {
        tmdbApi.getMovieDetails(movieId, API_KEY, "ko-KR").enqueue(object : Callback<MovieDetailsResponse> {
            override fun onResponse(call: Call<MovieDetailsResponse>, response: Response<MovieDetailsResponse>) {
                if (response.isSuccessful) {
                    val movieDetails = response.body()
                    movieDetails?.let { displayMovieDetails(it) }// 영화 상세 정보를 화면에 표시하는 메서드를 호출한다.
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
        menuInflater.inflate(R.menu.menu_detail, menu)// 메뉴 리소스를 인플레이트한다.
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                val intent = Intent(this, MainActivity::class.java) // 홈 버튼 클릭 시 MainActivity로 이동한다.
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

        // Picasso 라이브러리를 사용해 영화 포스터 이미지를 로드한다.
        Picasso.get().load("https://image.tmdb.org/t/p/w500${movie.poster_path}").into(moviePoster)
    }
}
