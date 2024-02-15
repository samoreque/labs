package com.code.path.flixter

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.code.path.flixter.domain.MoviesRepository

private const val LIST_STATE_KEY = "position"
private const val UPCOMING_LIST_STATE_KEY = "upcoming_position"

class MainActivity : AppCompatActivity() {
    private lateinit var moviesAdapter: ItemsAdapter
    private lateinit var upcomingMoviesAdapter: ItemsAdapter
    private lateinit var itemsRecyclerView: RecyclerView
    private lateinit var upcomingItemsRecyclerView: RecyclerView
    private val moviesRepository = MoviesRepository()

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        val position = (itemsRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        state.putInt(LIST_STATE_KEY, position)

        val upcomingPosition = (upcomingItemsRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        state.putInt(UPCOMING_LIST_STATE_KEY, upcomingPosition)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        setContentView(R.layout.main_activity)
        initMoviesAdapter(displayMetrics, savedInstanceState)
        initUpcomingMoviesAdapter(displayMetrics, savedInstanceState)
    }

    private fun initMoviesAdapter(displayMetrics: DisplayMetrics, savedInstanceState: Bundle?) {
        itemsRecyclerView = findViewById(R.id.itemsRecyclerView)
        moviesAdapter = ItemsAdapter(orientationMetrics = OrientationMetrics.get(this, displayMetrics)) { movieId, view ->
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@MainActivity,
                view, "imageMovie"
            )
            startActivity(MovieDetailActivity.getIntent(this, movieId), options.toBundle())
        }
        itemsRecyclerView.adapter = moviesAdapter
        itemsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val position = savedInstanceState?.let { savedInstanceState.getInt(LIST_STATE_KEY, 0) } ?: 0
        if (moviesAdapter.items.isEmpty()) {
            fetchMovies(position)
        }
    }

    private fun initUpcomingMoviesAdapter(displayMetrics: DisplayMetrics, savedInstanceState: Bundle?) {
        upcomingItemsRecyclerView = findViewById(R.id.upcomingRecyclerView)
        upcomingMoviesAdapter = ItemsAdapter(orientationMetrics = OrientationMetrics.get(this, displayMetrics)) { movieId, view ->
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@MainActivity,
                view, "imageMovie"
            )
            startActivity(MovieDetailActivity.getIntent(this, movieId), options.toBundle())
        }
        upcomingItemsRecyclerView.adapter = upcomingMoviesAdapter
        upcomingItemsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val position = savedInstanceState?.let { savedInstanceState.getInt(UPCOMING_LIST_STATE_KEY, 0) } ?: 0
        if (upcomingMoviesAdapter.items.isEmpty()) {
            fetchUpcomingMovies(position)
        }
    }

    private fun fetchMovies(currentPosition: Int) {
        moviesRepository.fetchMovies {
            it.getOrNull()?.also { movies ->
                moviesAdapter.updateItems(movies)
                itemsRecyclerView.scrollToPosition(currentPosition)
            }
        }
    }

    private fun fetchUpcomingMovies(currentPosition: Int) {
        moviesRepository.fetchUpcomingMovies {
            it.getOrNull()?.also { movies ->
                upcomingMoviesAdapter.updateItems(movies)
                upcomingItemsRecyclerView.scrollToPosition(currentPosition)
            }
        }
    }
}
