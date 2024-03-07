package com.code.path.bitfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.code.path.bitfit.data.db.BitFitDatabase
import com.code.path.bitfit.data.db.entity.SleepDataEntity
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val LIST_STATE_KEY = "position"

class SleepInputFragment : Fragment() {
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

        db = (this.activity?.application as BitFitApplication).db
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sleep_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initAdapter(view: View) {
        itemsRecyclerView = view.findViewById(R.id.itemsRecyclerView)
        moviesAdapter = ItemsAdapter()
        itemsRecyclerView.adapter = moviesAdapter
        itemsRecyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    private fun collectList() {
        lifecycleScope.launch {
            db.sleepDao().getSleeps().collect {
                moviesAdapter.updateItems(it)
            }
        }
    }

    private fun saveSleepData(view: View) {
        val hours = view.findViewById<Slider>(R.id.hoursSeekBar).value
        val feeling = view.findViewById<Slider>(R.id.feelingSeekBar).value
        val notes = view.findViewById<EditText>(R.id.notesEditText).text
        lifecycleScope.launch {
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
            this@SleepInputFragment.activity?.hideKeyboard()
        }
    }

    private fun initViews(view: View) {
        hourSlider = view.findViewById(R.id.hoursSeekBar)
        feelSlider = view.findViewById(R.id.feelingSeekBar)
        notesEditText = view.findViewById(R.id.notesEditText)
        initAdapter(view)
        collectList()
        view.findViewById<Button>(R.id.saveButton).setOnClickListener {
            saveSleepData(view)
        }
        view.findViewById<ImageButton>(R.id.photoButton).setOnClickListener {
            uriPhotoString = cameraHelper.dispatchTakePictureIntent(this.activity)?.toString()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SleepInputFragment()
    }
}