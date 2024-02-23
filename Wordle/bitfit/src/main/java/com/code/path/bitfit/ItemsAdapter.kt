package com.code.path.bitfit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.code.path.bitfit.data.db.dao.SleepStatistics
import com.code.path.bitfit.data.db.entity.SleepDataEntity
import java.text.SimpleDateFormat
import java.util.Locale

typealias movieSelected = (Int, View) -> Unit

class ItemsAdapter(
    private var items: List<SleepDataEntity> = emptyList(),
) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hoursTextView: TextView = itemView.findViewById(R.id.hoursTextView)
        val feelingTextView: TextView = itemView.findViewById(R.id.feelingTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val noteTextView: TextView = itemView.findViewById(R.id.notesTextView)
        val movieImageView: ImageView = itemView.findViewById(R.id.movieImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: ItemsAdapter.ViewHolder, position: Int) {
        val dateFormat = SimpleDateFormat("MMM dd yyyy", Locale.US)
        val context = viewHolder.itemView.context
        val item: SleepDataEntity = items[position]
        viewHolder.apply {
            hoursTextView.text = context.getString(R.string.hours_template, SleepStatistics.getHoursAverageCopy(item.sleepHours))
            feelingTextView.text = context.getString(R.string.feeling_template, SleepStatistics.getFeelingAverageCopy(item.feelingRate))
            noteTextView.text = item.notes
            dateTextView.text = dateFormat.format(item.createdAt)
            Glide.with(viewHolder.itemView.context)
                .load(item.mediaImageUrl)
                .placeholder(R.drawable.placeholer)
                .transform(RoundedCorners(50))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(movieImageView)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newMovies: List<SleepDataEntity>) {
        items = newMovies
        notifyDataSetChanged()
    }
}
