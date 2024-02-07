package com.code.path.flixter.data

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("results")
    val movies: List<Movie>
) {
    companion object {
        fun parseJSON(response: String?): MovieResponse? {
            val gson = GsonBuilder().create()
            return gson.fromJson(response, MovieResponse::class.java)
        }
    }
}

data class Movie(
    @SerializedName("original_title")
    val title: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String,
)
