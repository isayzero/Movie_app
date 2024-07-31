package com.example.movie_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.CalendarDay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var tmdbApi: TMDbApi
    private val API_KEY = "4dbe136f0b014f494c23256f91bf9545"
    private val BASE_URL = "https://api.themoviedb.org/3/"
    private val TAG = "MainActivity"
    private val allMovies = mutableListOf<Movie>()
    private lateinit var recommendationSlider: RecyclerView
    private lateinit var recommendationAdapter: MovieAdapter
    private val recommendedMovies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 뷰 초기화
        calendarView = findViewById(R.id.calendarView)
        recommendationSlider = findViewById(R.id.recommendationSlider)

        // 리사이클러뷰 및 레트로핏 설정
        setupRecyclerView()
        setupRetrofit()
        fetchAllMovies()

        // 달력 날짜 선택 리스너 설정
        calendarView.setOnDateChangedListener { _, date, _ ->
            val selectedDate = "${date.year}-${String.format("%02d", date.month)}-${String.format("%02d", date.day)}"
            fetchMoviesByDate(selectedDate)
        }

        checkLoginStatus()
    }

    // 리사이클러뷰 설정
    private fun setupRecyclerView() {
        recommendationAdapter = MovieAdapter(recommendedMovies, this)
        recommendationSlider.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recommendationSlider.adapter = recommendationAdapter
    }

    // 레트로핏 설정
    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        tmdbApi = retrofit.create(TMDbApi::class.java)
    }

    // 모든 영화 데이터 가져오기
    private fun fetchAllMovies() {
        val countries = listOf("KR", "US")
        countries.forEach { country ->
            fetchUpcomingMoviesByCountry(country, 1)
            fetchPastMoviesByCountry(country, "1900-01-01", "2024-12-31", 1)
        }
    }

    // 특정 국가의 개봉 예정 영화 가져오기
    private fun fetchUpcomingMoviesByCountry(country: String, page: Int) {
        tmdbApi.getUpcomingMoviesByRegion(API_KEY, "ko-KR", country, page).enqueue(object :
            Callback<UpcomingMoviesResponse> {
            override fun onResponse(
                call: Call<UpcomingMoviesResponse>,
                response: Response<UpcomingMoviesResponse>
            ) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    Log.d(TAG, "Fetched ${movies.size} upcoming movies for country $country on page $page")
                    addMoviesToAllMovies(movies)
                    if (movies.isNotEmpty()) {
                        fetchUpcomingMoviesByCountry(country, page + 1)
                    }
                    updateCalendarWithMovies()
                    filterRecommendedMovies()
                } else {
                    Log.e(TAG, "API call failed with response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UpcomingMoviesResponse>, t: Throwable) {
                Log.e(TAG, "API call failed with error: ${t.message}")
            }
        })
    }

    // 특정 국가의 이전 개봉 영화 가져오기
    private fun fetchPastMoviesByCountry(country: String, startDate: String, endDate: String, page: Int) {
        tmdbApi.getMoviesByDateRange(API_KEY, "ko-KR", startDate, endDate, country, page).enqueue(object :
            Callback<UpcomingMoviesResponse> {
            override fun onResponse(
                call: Call<UpcomingMoviesResponse>,
                response: Response<UpcomingMoviesResponse>
            ) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    Log.d(TAG, "Fetched ${movies.size} past movies for country $country")
                    addMoviesToAllMovies(movies)
                    if (movies.isNotEmpty()) {
                        fetchPastMoviesByCountry(country, startDate, endDate, page + 1)
                    }
                    updateCalendarWithMovies()
                    filterRecommendedMovies()
                } else {
                    Log.e(TAG, "API call failed with response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UpcomingMoviesResponse>, t: Throwable) {
                Log.e(TAG, "API call failed with error: ${t.message}")
            }
        })
    }

    // 영화 리스트에 영화 추가
    private fun addMoviesToAllMovies(movies: List<Movie>) {
        movies.forEach { movie ->
            if (!allMovies.any { it.id == movie.id }) {
                allMovies.add(movie)
            }
        }
    }

    // 특정 날짜에 개봉한 영화 가져오기
    private fun fetchMoviesByDate(date: String) {
        Log.d(TAG, "Fetching movies for date: $date")
        val moviesOnDate = allMovies.filter { it.release_date == date }
        Log.d(TAG, "Movies on $date: ${moviesOnDate.size}")
        if (moviesOnDate.isNotEmpty()) {
            navigateToCalDetail(moviesOnDate, date)
        } else {
            Toast.makeText(this@MainActivity, "개봉하는 영화가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 달력에 영화 날짜 표시
    private fun updateCalendarWithMovies() {
        val validDates = allMovies.mapNotNull { movie ->
            val dateParts = movie.release_date.split("-")
            try {
                val year = dateParts[0].toInt()
                val month = dateParts[1].toInt()
                val day = dateParts[2].toInt()
                CalendarDay.from(year, month, day)
            } catch (e: Exception) {
                Log.e(TAG, "Invalid date found: ${movie.release_date} - Error: ${e.message}")
                null
            }
        }
        calendarView.removeDecorators()
        calendarView.addDecorator(CircleDecorator(this))
        calendarView.addDecorator(EventDecorator(validDates, this))
    }

    // 영화 세부 정보 화면으로 이동
    private fun navigateToCalDetail(movies: List<Movie>, releaseDate: String) {
        val intent = Intent(this, CalDetailActivity::class.java)
        val gson = Gson()
        val moviesJson = gson.toJson(movies)
        intent.putExtra("movies", moviesJson)
        intent.putExtra("release_date", releaseDate)
        startActivity(intent)
    }

    // 액티비티 재개 시 추천 영화 랜덤으로 갱신
    override fun onResume() {
        super.onResume()
        randomizeRecommendedMovies()
    }

    // 추천 영화 목록 랜덤화
    private fun randomizeRecommendedMovies() {
        recommendedMovies.clear()
        val randomMovies = allMovies.shuffled().take(10)
        recommendedMovies.addAll(randomMovies)
        Log.d(TAG, "Randomized recommended movies: ${recommendedMovies.size}")
        recommendationAdapter.notifyDataSetChanged()
    }

    // 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
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

    // 메뉴 항목 선택
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                val userId = intent.getStringExtra("user_Id")
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("user_Id", userId)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 영화 검색
    private fun searchMovies(query: String) {
        tmdbApi.searchMovies(API_KEY, "ko-KR", query, 1).enqueue(object : Callback<UpcomingMoviesResponse> {
            override fun onResponse(
                call: Call<UpcomingMoviesResponse>,
                response: Response<UpcomingMoviesResponse>
            ) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    if (movies.isNotEmpty()) {
                        navigateToCalDetail(movies, "검색 결과")
                    } else {
                        Toast.makeText(this@MainActivity, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
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

    // 로그인 상태 확인
    private fun checkLoginStatus() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (!isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // 추천 영화 필터링
    private fun filterRecommendedMovies() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userGenres = sharedPreferences.getString("userGenres", "") ?: ""
        val genresList = userGenres.split(", ").map { it.trim() }

        Log.d(TAG, "User genres: $genresList")

        recommendedMovies.clear()
        recommendedMovies.addAll(allMovies.filter { movie ->
            movie.genre_ids.any { genreId ->
                genresList.contains(getGenreNameById(genreId))
            }
        })

        Log.d(TAG, "Filtered recommended movies: ${recommendedMovies.size}")
        recommendationAdapter.notifyDataSetChanged()
    }

    // 장르 ID를 장르 이름으로 변환
    private fun getGenreNameById(genreId: Int): String {
        return when (genreId) {
            28 -> "액션"
            35 -> "코미디"
            18 -> "드라마"
            12 -> "어드벤처"
            16 -> "애니메이션"
            80 -> "범죄"
            99 -> "다큐멘터리"
            10751 -> "가족"
            14 -> "판타지"
            36 -> "역사"
            27 -> "공포"
            10402 -> "음악"
            9648 -> "미스터리"
            10749 -> "로맨스"
            878 -> "SF"
            10770 -> "TV 영화"
            53 -> "스릴러"
            10752 -> "전쟁"
            37 -> "서부"
            else -> "기타"
        }
    }
}
