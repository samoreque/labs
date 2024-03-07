package com.code.path.bitfit

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.code.path.bitfit.data.db.BitFitDatabase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch

class SleepDashboardFragment : Fragment(), SeekBar.OnSeekBarChangeListener,
    OnChartValueSelectedListener {
    private lateinit var db: BitFitDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = (this.activity?.application as BitFitApplication).db
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sleep_dashboard_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun collectAverage(view: View) {
        lifecycleScope.launch {
            db.sleepDao().getSleepStatistics().collect {
                view.findViewById<TextView>(R.id.averageHoursTextView).text =
                    getString(R.string.average_hours_of_sleep, it.getHoursAverageCopy)
                view.findViewById<TextView>(R.id.averageFeelingTextView).text =
                    getString(R.string.average_feeling, it.getFeelingAverageCopy)
            }
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            db.sleepDao().getSleepPointData().collect {
                val values1 = ArrayList<Entry>()
                val values2 = ArrayList<Entry>()
                it.mapIndexed { i, data ->
                    values1.add(Entry(i.toFloat(), data.hour))
                    values2.add(Entry(i.toFloat(), data.feeling.toFloat()))
                }
                setData(values1, values2)
            }
        }
    }

    private fun initViews(view: View) {
        initChart(view)
        collectAverage(view)
        collectData()
    }

    private var chart: LineChart? = null

    private fun initChart(view: View) {
        chart = view.findViewById(R.id.chart1)
        chart!!.setOnChartValueSelectedListener(this)
        chart!!.description.isEnabled = false
        chart!!.setTouchEnabled(true)
        chart!!.dragDecelerationFrictionCoef = 0.9f
        chart!!.isDragEnabled = true
        chart!!.setScaleEnabled(true)
        chart!!.setDrawGridBackground(false)
        chart!!.isHighlightPerDragEnabled = true
        chart!!.setPinchZoom(true)


        chart!!.setBackgroundColor(Color.TRANSPARENT)
        chart!!.animateX(1500)

        val l = chart!!.legend

        l.form = LegendForm.LINE
        l.textSize = 11f
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        val xAxis = chart!!.xAxis
        xAxis.textSize = 11f
        xAxis.textColor = Color.WHITE
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        val leftAxis = chart!!.axisLeft
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.axisMaximum = 24f
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true
        chart!!.axisRight.isEnabled = false
    }

    private fun setData(values1: ArrayList<Entry>, values2: ArrayList<Entry>) {
        val set1: LineDataSet
        val set2: LineDataSet
        if (chart!!.data != null &&
            chart!!.data.dataSetCount > 0
        ) {
            set1 = chart!!.data.getDataSetByIndex(0) as LineDataSet
            set2 = chart!!.data.getDataSetByIndex(1) as LineDataSet
            set1.values = values1
            set2.values = values2
            chart!!.data.notifyDataChanged()
            chart!!.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values1, "Hours")
            set1.axisDependency = AxisDependency.LEFT
            set1.color = Color.GREEN
            set1.setCircleColor(Color.GREEN)
            set1.lineWidth = 2f
            set1.circleRadius = 3f
            set1.fillAlpha = 65
            set1.fillColor = Color.GREEN
            set1.highLightColor = Color.GREEN
            set1.setDrawCircleHole(false)
            set1.valueTextColor = Color.GREEN

            set2 = LineDataSet(values2, "Feeling")
            set2.axisDependency = AxisDependency.LEFT
            set2.color = ColorTemplate.getHoloBlue()
            set2.setCircleColor(ColorTemplate.getHoloBlue())
            set2.lineWidth = 2f
            set2.circleRadius = 3f
            set2.fillAlpha = 65
            set2.fillColor = ColorTemplate.getHoloBlue()
            set2.setDrawCircleHole(false)
            set2.highLightColor = ColorTemplate.getHoloBlue()
            set2.valueTextColor = ColorTemplate.getHoloBlue()

            val data = LineData(set1, set2)
            data.setValueTextSize(9f)
            chart!!.data = data
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        chart!!.invalidate()
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        Log.i("Entry selected", e.toString())
        chart!!.centerViewToAnimated(
            e.x, e.y, chart!!.data.getDataSetByIndex(h.dataSetIndex)
                .axisDependency, 500
        )
    }

    override fun onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.")
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    companion object {
        @JvmStatic
        fun newInstance() = SleepDashboardFragment()
    }
}