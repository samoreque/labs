package com.code.path.bitfit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.code.path.bitfit.data.db.BitFitDatabase
import com.code.path.bitfit.data.db.entity.SleepDataEntity
import com.google.android.material.slider.Slider
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val LIST_STATE_KEY = "position"

class MainActivity : AppCompatActivity() {
    private lateinit var moviesAdapter: ItemsAdapter
    private lateinit var itemsRecyclerView: RecyclerView
    private lateinit var hourSlider: Slider
    private lateinit var feelSlider: Slider
    private lateinit var notesEditText: EditText
    private lateinit var db: BitFitDatabase
    private val cameraHelper = CameraHelper()
    private var uriPhotoString: String? = null

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        val position = (itemsRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        state.putInt(LIST_STATE_KEY, position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        hourSlider = findViewById(R.id.hoursSeekBar)
        feelSlider = findViewById(R.id.feelingSeekBar)
        notesEditText = findViewById(R.id.notesEditText)
        db = (application as BitFitApplication).db
        collectAverage()
        initAdapter(savedInstanceState)
        collectList()
        findViewById<Button>(R.id.saveButton).setOnClickListener {
            saveSleepData()
        }
        findViewById<ImageButton>(R.id.photoButton).setOnClickListener {
            uriPhotoString = cameraHelper.dispatchTakePictureIntent(this)?.toString()
        }
    }

    private fun initAdapter(savedInstanceState: Bundle?) {
        itemsRecyclerView = findViewById(R.id.itemsRecyclerView)
        moviesAdapter = ItemsAdapter()
        itemsRecyclerView.adapter = moviesAdapter
        itemsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun collectAverage() {
        lifecycleScope.launch {
            db.sleepDao().getSleepStatistics().collect {
                findViewById<TextView>(R.id.averageHoursTextView).text = getString(R.string.average_hours_of_sleep, it.getHoursAverageCopy)
                findViewById<TextView>(R.id.averageFeelingTextView).text = getString(R.string.average_feeling, it.getFeelingAverageCopy)
            }
        }
    }

    private fun collectList() {
        lifecycleScope.launch {
            db.sleepDao().getSleeps().collect {
                moviesAdapter.updateItems(it)
            }
        }
    }

    private fun saveSleepData() {
        val hours = findViewById<Slider>(R.id.hoursSeekBar).value
        val feeling = findViewById<Slider>(R.id.feelingSeekBar).value
        val notes = findViewById<EditText>(R.id.notesEditText).text
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                db.sleepDao().insert(
                    SleepDataEntity(
                        sleepHours = hours,
                        feelingRate = feeling.toInt(),
                        notes = "$notes",
                        mediaImageUrl = uriPhotoString,
                    )
                )
            }
            hourSlider.value = 0f
            feelSlider.value = 0f
            notesEditText.setText("")
            uriPhotoString = null
            hideKeyboard()
        }
    }
}

private fun Activity.hideKeyboard() {
    this.currentFocus?.let { view ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
