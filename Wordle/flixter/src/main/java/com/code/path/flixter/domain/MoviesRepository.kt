package com.code.path.flixter.domain

import android.util.Log
import com.code.path.flixter.data.Movie
import com.code.path.flixter.data.MovieResponse
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler
import okhttp3.Headers

private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val MOVIES_URL = "movie/now_playing"
private const val UPCOMING_MOVIES_URL = "movie/upcoming"
private const val MOVIE_URL = "movie/"
private const val API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed"

typealias ResultListener<T> = (Result<T>) -> Unit

class MoviesRepository {
    private val client = AsyncHttpClient()
    fun fetchMovies(listener: ResultListener<List<Movie>>) {
        call(url = MOVIES_URL, listener = listener) {
            MovieResponse.parseJSON(it)?.movies
        }
    }

    fun fetchUpcomingMovies(listener: ResultListener<List<Movie>>) {
        val params = RequestParams()
        params["language"] = "en-US"
        params["page"] = "1"
        call(url = UPCOMING_MOVIES_URL, listener = listener) {
            MovieResponse.parseJSON(it)?.movies
        }
    }

    fun fetchMovie(movieId: Int, listener: ResultListener<Movie>) {
        val params = RequestParams()
        params["language"] = "en-US"
        call(url = "$MOVIE_URL/$movieId", listener = listener) {
            Movie.parseJSON(it)
        }
    }

    private fun <T> call(url: String, params: RequestParams = RequestParams(), listener: ResultListener<T>, parse: (String) -> T?) {
        params["api_key"] = API_KEY
        client["$BASE_URL$url", params, object : TextHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, response: String) {
                listener.invoke(
                    parse(response)?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty Result"))
                )
            }

            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, t: Throwable?) {
                onFailureDefault(listener, errorResponse, t)
            }
        }]
    }

    private fun <T> onFailureDefault(listener: ResultListener<T>, errorResponse: String, t: Throwable?) {
        Log.e("response", "error: $errorResponse")
        val exception = t ?: Throwable(errorResponse)
        listener.invoke(Result.failure(exception))
    }
}
