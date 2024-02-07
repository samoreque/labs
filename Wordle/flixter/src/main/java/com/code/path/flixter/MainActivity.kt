package com.code.path.flixter

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.code.path.flixter.data.MovieResponse
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler
import okhttp3.Headers

private const val BASE_URL = "https://api.themoviedb.org/3/movie/now_playing"
private const val API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed"
private const val LIST_STATE_KEY = "position"

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ItemsAdapter
    private lateinit var itemsRecyclerView: RecyclerView

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        val position = (itemsRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        state.putInt(LIST_STATE_KEY, position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        setContentView(R.layout.main_activity)
        itemsRecyclerView = findViewById(R.id.itemsRecyclerView)
        adapter = ItemsAdapter(orientationMetrics = OrientationMetrics.get(this, displayMetrics))
        itemsRecyclerView.adapter = adapter
        itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        val position = savedInstanceState?.let { savedInstanceState.getInt(LIST_STATE_KEY, 0) } ?: 0
        if (adapter.items.isEmpty()) {
            fetchMovies(position)
        }
    }

    private fun fetchMovies(currentPosition: Int) {
        val client = AsyncHttpClient()
        val params = RequestParams()
        params["api_key"] = API_KEY
        client[BASE_URL, params, object : TextHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, response: String) {
                MovieResponse.parseJSON(response)?.also {
                    adapter.updateItems(it.movies)
                    itemsRecyclerView.scrollToPosition(currentPosition)
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, t: Throwable?) {
                Log.e("response", "error: $errorResponse")
            }
        }]
    }
}
