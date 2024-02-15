package com.code.path.flixter

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import com.code.path.flixter.data.Movie

typealias movieSelected = (Int, View) -> Unit

class ItemsAdapter(
    var items: List<Movie> = emptyList(),
    private val orientationMetrics: OrientationMetrics,
    private val movieSelected: movieSelected
) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val movieImageView: ImageView = itemView.findViewById(R.id.movieImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.banner_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: ItemsAdapter.ViewHolder, position: Int) {
        val item: Movie = items[position]
        viewHolder.apply {
            itemView.setOnClickListener {
                movieSelected.invoke(item.id, movieImageView)
            }
            titleTextView.text = item.title
            Glide.with(viewHolder.itemView.context)
                .load("https://image.tmdb.org/t/p/w500${item.posterPath}")
                .placeholder(R.drawable.placeholer)
                .applyOrientation(orientationMetrics)
                .transform( RoundedCorners(50))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(movieImageView)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newMovies: List<Movie>) {
        items = newMovies
        notifyDataSetChanged()
    }
}

sealed interface OrientationMetrics {
    fun applyChanges(builder: RequestBuilder<Drawable>): RequestBuilder<Drawable>
    data class Portrait(val width: Int) : OrientationMetrics {
        override fun applyChanges(builder: RequestBuilder<Drawable>): RequestBuilder<Drawable> {
            return builder.override((width * 0.4f).toInt(), Target.SIZE_ORIGINAL)
        }
    }

    data class Landscape(val width: Int) : OrientationMetrics {
        override fun applyChanges(builder: RequestBuilder<Drawable>): RequestBuilder<Drawable> {
            return builder.override((width * 0.5f).toInt(), 500).centerCrop()
        }
    }

    companion object {
        fun get(context: Context, displayMetrics: DisplayMetrics): OrientationMetrics {
            val width = displayMetrics.widthPixels
            return when (context.resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> Landscape(width)
                else -> Portrait(width)
            }
        }
    }
}

fun RequestBuilder<Drawable>.applyOrientation(orientationMetrics: OrientationMetrics) = orientationMetrics.applyChanges(this)
