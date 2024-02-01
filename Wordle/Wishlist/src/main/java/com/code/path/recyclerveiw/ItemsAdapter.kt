package com.code.path.recyclerveiw

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(private val items: List<Item>, private val listener: ItemListener) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameText)
        val itemPriceTextView: TextView = itemView.findViewById(R.id.priceText)
        val urlTextView: TextView = itemView.findViewById(R.id.urlText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: ItemsAdapter.ViewHolder, position: Int) {
        viewHolder.itemView.setOnLongClickListener {
            listener.removeItem(position)
            true
        }
        val item: Item = items[position]
        viewHolder.apply {
            itemNameTextView.text = item.name
            itemPriceTextView.text = item.price.toString()
            urlTextView.text = item.url
            urlTextView.setOnClickListener {
                listener.loadUrl(item)
            }
        }
    }

    override fun getItemCount() = items.size
}

interface ItemListener {
    fun removeItem(index: Int)
    fun loadUrl(item: Item)
}
