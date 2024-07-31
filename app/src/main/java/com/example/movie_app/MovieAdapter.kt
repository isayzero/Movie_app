package com.example.movie_app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MovieAdapter(private val movies: List<Movie>, private val context: Context) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.poster_movie, parent, false)
        return MovieViewHolder(view) // 뷰 홀더를 생성하는 부분이다.
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie, context) // 뷰 홀더와 데이터를 바인딩하는 부분이다.
    }

    override fun getItemCount(): Int {
        return movies.size // 영화 목록의 총 개수를 반환하는 부분이다.
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.imgPoster)
        private val title: TextView = itemView.findViewById(R.id.txtTitle)

        fun bind(movie: Movie, context: Context) {
            title.text = movie.title // 영화 제목을 설정하는 부분이다.
            Picasso.get().load("https://image.tmdb.org/t/p/w500${movie.poster_path}").into(poster)

            itemView.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("movie_id", movie.id)
                context.startActivity(intent) // 아이템 클릭 시 상세 액티비티로 이동하는 부분이다.
            }
        }
    }
}
