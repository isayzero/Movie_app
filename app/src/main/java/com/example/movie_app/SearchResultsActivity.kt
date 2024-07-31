package com.example.movie_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var tmdbApi: TMDbApi
    private val API_KEY = "4dbe136f0b014f494c23256f91bf9545"
    private val TAG = "SearchResultsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        val searchQuery = intent.getStringExtra("search_query")
        val moviesJson = intent.getStringExtra("movies")

        val gson = Gson()
        val type = object : TypeToken<List<Movie>>() {}.type
        val movies: List<Movie> = gson.fromJson(moviesJson, type)

        val txtSearchQuery: TextView = findViewById(R.id.txtSearchQuery)
        txtSearchQuery.text = searchQuery ?: "Search results"

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewSearchResults)
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
        val searchView = searchItem?.actionView as SearchView

        searchView.queryHint = "영화 제목을 입력하세요"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchMovies(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
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
        tmdbApi.searchMovies(API_KEY, "ko-KR", query, 1).enqueue(object : Callback<UpcomingMoviesResponse> {
            override fun onResponse(
                call: Call<UpcomingMoviesResponse>,
                response: Response<UpcomingMoviesResponse>
            ) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    if (movies.isNotEmpty()) {
                        navigateToSearchResults(movies, query)
                    } else {
                        Toast.makeText(this@SearchResultsActivity, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
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
        val intent = Intent(this, SearchResultsActivity::class.java)
        val gson = Gson()
        val moviesJson = gson.toJson(movies)
        intent.putExtra("movies", moviesJson)
        intent.putExtra("search_query", searchQuery)
        startActivity(intent)
    }
}
