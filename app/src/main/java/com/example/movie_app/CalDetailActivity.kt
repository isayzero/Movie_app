package com.example.movie_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CalDetailActivity : AppCompatActivity() {

    private lateinit var tmdbApi: TMDbApi
    private val API_KEY = "4dbe136f0b014f494c23256f91bf9545"
    private val TAG = "CalDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cal_detail)

        val releaseDate = intent.getStringExtra("release_date")
        val moviesJson = intent.getStringExtra("movies")

        val gson = Gson()
        val type = object : TypeToken<List<Movie>>() {}.type
        val movies: List<Movie> = gson.fromJson(moviesJson, type)

        val txtReleaseDate: TextView = findViewById(R.id.txtReleaseDate)
        txtReleaseDate.text = releaseDate ?: "No release date available"

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewMovies)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MovieAdapter(movies, this)

        setupRetrofit()
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        tmdbApi = retrofit.create(TMDbApi::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? androidx.appcompat.widget.SearchView

        searchView?.queryHint = "영화 제목을 입력하세요"
        searchView?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchMovies(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 텍스트 변경 시 동작을 정의할 수 있습니다.
                return true
            }
        })

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

    private fun searchMovies(query: String) {
        tmdbApi.searchMovies(API_KEY, "ko-KR", query, 1).enqueue(object :
            Callback<UpcomingMoviesResponse> {
            override fun onResponse(
                call: Call<UpcomingMoviesResponse>,
                response: Response<UpcomingMoviesResponse>
            ) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    if (movies.isNotEmpty()) {
                        navigateToSearchResults(movies, "검색 결과")
                    } else {
                        Toast.makeText(this@CalDetailActivity, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(TAG, "API 호출 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UpcomingMoviesResponse>, t: Throwable) {
                Log.e(TAG, "API 호출 실패: ${t.message}")
            }
        })
    }

    private fun navigateToSearchResults(movies: List<Movie>, searchQuery: String) {
        val intent = Intent(this, SearchResultsActivity::class.java)  // 여기에서 CalDetailActivity가 아닌 SearchResultsActivity를 사용합니다.
        val gson = Gson()
        val moviesJson = gson.toJson(movies)
        intent.putExtra("movies", moviesJson)
        intent.putExtra("search_query", searchQuery)
        startActivity(intent)
    }
}
