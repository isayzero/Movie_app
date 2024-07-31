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
    private val API_KEY = "4dbe136f0b014f494c23256f91bf9545" // TMDB에서 발급 받은 API 키
    private val TAG = "CalDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cal_detail)

        val releaseDate = intent.getStringExtra("release_date")// Intent로부터 release_date 값을 가져온다.
        val moviesJson = intent.getStringExtra("movies")// Intent로부터 movies 값을 가져온다.

        val gson = Gson()
        val type = object : TypeToken<List<Movie>>() {}.type
        val movies: List<Movie> = gson.fromJson(moviesJson, type)// JSON 문자열을 List<Movie> 객체로 변환한다.

        val txtReleaseDate: TextView = findViewById(R.id.txtReleaseDate)
        txtReleaseDate.text = releaseDate ?: "No release date available"// release_date를 TextView에 설정한다.

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewMovies)
        recyclerView.layoutManager = LinearLayoutManager(this)// RecyclerView를 LinearLayout으로 설정한다.
        recyclerView.adapter = MovieAdapter(movies, this)// RecyclerView에 어댑터를 설정한다.

        setupRetrofit()// Retrofit 설정을 초기화한다.
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")// TMDb API의 기본 URL을 설정한다.
            .addConverterFactory(GsonConverterFactory.create())// JSON 변환을 위해 GsonConverterFactory를 추가한다.
            .build()
        tmdbApi = retrofit.create(TMDbApi::class.java)// TMDbApi 인터페이스를 구현한 객체를 생성한다.
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)// 메뉴 리소스를 인플레이트한다.
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? androidx.appcompat.widget.SearchView

        searchView?.queryHint = "영화 제목을 입력하세요" // 검색 힌트를 설정한다.
        searchView?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchMovies(it)// 검색어 제출 시 searchMovies 메서드를 호출한다.
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
                val intent = Intent(this, MainActivity::class.java)// 홈 버튼 클릭 시 MainActivity로 이동한다.
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
                    val movies = response.body()?.results ?: emptyList()// API 호출이 성공하면 영화 목록을 가져온다.
                    if (movies.isNotEmpty()) {
                        navigateToSearchResults(movies, "검색 결과")// 검색 결과가 있으면 navigateToSearchResults 메서드를 호출한다.
                    } else {
                        Toast.makeText(this@CalDetailActivity, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()// 검색 결과가 없으면 토스트 메시지를 표시한다.
                    }
                } else {
                    Log.e(TAG, "API 호출 실패: ${response.errorBody()?.string()}")// API 호출이 실패하면 로그를 출력한다.
                }
            }

            override fun onFailure(call: Call<UpcomingMoviesResponse>, t: Throwable) {
                Log.e(TAG, "API 호출 실패: ${t.message}")// API 호출 자체가 실패하면 로그를 출력한다.
            }
        })
    }

    private fun navigateToSearchResults(movies: List<Movie>, searchQuery: String) {
        val intent = Intent(this, SearchResultsActivity::class.java)  // 검색 결과를 표시할 액티비티로 이동한다.
        val gson = Gson()
        val moviesJson = gson.toJson(movies)// 영화 목록을 JSON 문자열로 변환한다.
        intent.putExtra("movies", moviesJson)// 인텐트에 영화 목록을 추가한다.
        intent.putExtra("search_query", searchQuery) // 인텐트에 검색어를 추가한다.
        startActivity(intent)
    }
}
