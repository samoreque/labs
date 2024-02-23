package com.code.path.flixter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.code.path.flixter.domain.MoviesRepository

class MovieDetailActivity : AppCompatActivity() {
    private val moviesRepository = MoviesRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_detail)
        intent.extras?.getStringArray("array")?.let {
            Log.e("tag", it.toString())
        }
        fetchMovies(intent.getIntExtra(MOVIE_ID_PARAMETER, 0))
    }

    private fun fetchMovies(movieId: Int) {
        moviesRepository.fetchMovie(movieId) {
            it.getOrNull()?.also { movie ->
                findViewById<TextView>(R.id.titleTextView).text = movie.title
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .transform( RoundedCorners(50))
                    .into(findViewById(R.id.movieImageView))

                findViewById<TextView>(R.id.overviewTextView).text = movie.overview
                findViewById<TextView>(R.id.releaseTextView).text = getString(R.string.released, movie.releaseDate)
                findViewById<TextView>(R.id.originalTextView).text = getString(R.string.original, movie.originalTitle)
                findViewById<TextView>(R.id.popularityTextView).text = getString(R.string.popularity, movie.popularity.toString())
            }
        }
    }

    companion object {
        const val MOVIE_ID_PARAMETER = "movieId"
        fun getIntent(context: Context, movieId: Int): Intent {
            val myIntent = Intent(context, MovieDetailActivity::class.java)
            Bundle().apply {
                putStringArray("array",arrayOf("1","2","3"))

                myIntent.putExtras(this)
            }
            return myIntent
        }
    }
}
