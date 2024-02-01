package com.code.path.recyclerveiw

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), ItemListener {
    private val itemsList = mutableListOf<Item>()
    private lateinit var nameInput: EditText
    private lateinit var priceInput: EditText
    private lateinit var urlInput: EditText
    private lateinit var adapter: ItemsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        nameInput = findViewById(R.id.itemNameInput)
        priceInput = findViewById(R.id.priceInput)
        urlInput = findViewById(R.id.urlInput)
        val itemsRecyclerView: RecyclerView = findViewById(R.id.itemsRecyclerView)
        adapter = ItemsAdapter(itemsList, this)
        itemsRecyclerView.adapter = adapter
        itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        findViewById<Button>(R.id.submitButton).setOnClickListener {
            val nameItem: String = nameInput.text.toString()
            val price = priceInput.text.toString().toFloatOrNull() ?: 0f
            val url = urlInput.text.toString().addProtocol()
            itemsList.add(Item(nameItem, price, url))
            adapter.notifyItemInserted(itemsList.size - 1)
            nameInput.setText("")
            nameInput.requestFocus()
            priceInput.setText("")
            urlInput.setText("")
            hideKeyboard()
        }
    }

    override fun removeItem(index: Int) {
        itemsList.removeAt(index)
        adapter.notifyDataSetChanged()
    }

    override fun loadUrl(item: Item) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
            ContextCompat.startActivity(this, browserIntent, null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Invalid URL for " + item.name, Toast.LENGTH_LONG).show()
        } catch (e: Throwable) {
            Toast.makeText(this, "Invalid URL for " + item.name, Toast.LENGTH_LONG).show()
        }
    }
}

private fun Activity.hideKeyboard() {
    this.currentFocus?.let { view ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun String.addProtocol(): String {
    return if (!startsWith("http://") && !startsWith("https://")) {
        "http://$this"
    } else this
}
