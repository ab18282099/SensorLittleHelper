package com.example.user.siolSupervise.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.user.siolSupervise.R
import com.example.user.siolSupervise.db.HttpHelper
import com.example.user.siolSupervise.db.IHttpAction
import com.example.user.siolSupervise.dto.PhpUrlDto
import com.example.user.siolSupervise.fragments.ChartFragment
import com.example.user.siolSupervise.models.AppSettingModel
import com.example.user.siolSupervise.ui.chartEngine.MyChartFactory
import com.example.user.siolSupervise.utility.HttpRequest
import org.achartengine.chart.PointStyle
import org.achartengine.model.TimeSeries
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.renderer.XYSeriesRenderer
import org.apache.commons.beanutils.ConvertUtils
import org.jetbrains.anko.find
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class SetChartDialog constructor(context: Context, parentFragment: ChartFragment) : AlertDialog(context) {
    private val _context = context
    private val _appSettingModel = AppSettingModel(context)
    private val _parentFragment = parentFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_set_chart)
        val spinner_chart = findViewById<Spinner>(R.id.spinner_chart)
        val btn_set_chart = findViewById<Button>(R.id.btn_set_chart)
        var chartId = 0
        val sensorTitleList = arrayOfNulls<String>(_appSettingModel.SensorQuantity())

        for (i in 0 until _appSettingModel.SensorQuantity()) {
            sensorTitleList[i] = _appSettingModel.SensorName(i)
        }

        val sensorListAdapter = ArrayAdapter(_context, android.R.layout.simple_spinner_dropdown_item, sensorTitleList)
        spinner_chart.adapter = sensorListAdapter
        spinner_chart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                chartId = position
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
            }
        }

        btn_set_chart.setOnClickListener {
            SetChartView(chartId)
            this.dismiss()
        }
    }

    private fun SetChartView(chartID: Int) {
        val tx_chartTitle = _parentFragment.find<TextView>(R.id.tx_chartTitle)
        tx_chartTitle.text = _appSettingModel.SensorName(chartID)
        val buildChartHelper = HttpHelper.InitInstance(_context)
        buildChartHelper!!.SetHttpAction(object : IHttpAction {
            override fun OnHttpRequest() {
                ChartDataRenew(HttpRequest.DownloadFromMySQL("society",
                        PhpUrlDto(_context).LoadingWholeData), chartID)
            }

            override fun OnException(e: Exception) {
                _context.toast(e.toString())
            }

            override fun OnPostExecute() {
            }
        })
        buildChartHelper.StartHttpThread()
    }

    private fun ChartDataRenew(jsonString: String?, chartId: Int) {
        val jsonArray = JSONArray(jsonString)
        val interval = if (jsonArray.length() > 500) (jsonArray.length()).div(500) else 1
        val x = ArrayList<kotlin.Array<Date?>>()
        val y = ArrayList<DoubleArray>()
        val sensorData = arrayOfNulls<String>(jsonArray.length())
        val timeData = arrayOfNulls<Date>(jsonArray.length())

        for (i in 0 until jsonArray.length()) {
            timeData[i] = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) //HH: 24hr / hh: 12hr
                    .parse(jsonArray.getJSONObject(i).getString("time"))
            sensorData[i] = jsonArray.getJSONObject(i).getString("sensor_" + (chartId + 1).toString())
        }

        x.add(timeData)
        y.add(ConvertUtils.convert(sensorData, java.lang.Double.TYPE) as DoubleArray)
        val dataSet = BuildDataSet(_appSettingModel.SensorName(chartId), x, y, interval)
        val renderer = BuildRenderer(Color.BLUE, PointStyle.CIRCLE, true,
                "數據折線圖", "TIME", "%",
                timeData[0]!!.time.toDouble(),
                timeData[timeData.size - 1]!!.time.toDouble(),
                (-50).toDouble(), (100).toDouble(), Color.BLACK)

        _context.runOnUiThread {
            val chartLayout = _parentFragment.find<LinearLayout>(R.id.chartLayout)
            chartLayout.removeAllViewsInLayout()
            val chart = MyChartFactory.getTimeChartView(_context, dataSet, renderer, null)
            chart.setOnTouchListener { _, _ ->
                chart.parent.requestDisallowInterceptTouchEvent(true)
                false
            }

            chartLayout.addView(chart)
        }
    }

    private fun BuildDataSet(title: String, xValue: ArrayList<kotlin.Array<Date?>>,
                             yVale: ArrayList<DoubleArray>, interval: Int): XYMultipleSeriesDataset {
        val dataSet = XYMultipleSeriesDataset()
        val series = TimeSeries(title)
        val xV = xValue[0]
        val yV = yVale[0]

        for (i in 0 until xV.size step interval) {
            series.add(xV[i], yV[i])
        }
        dataSet.addSeries(series)
        return dataSet
    }

    private fun BuildRenderer(color: Int, style: PointStyle, fill: Boolean,
                              title: String, xTitle: String,
                              yTitle: String, xMin: Double, xMax: Double,
                              yMin: Double, yMax: Double, axisColor: Int): XYMultipleSeriesRenderer {
        val renderer = XYMultipleSeriesRenderer()
        val singleRenderer = XYSeriesRenderer()
        singleRenderer.color = color
        singleRenderer.pointStyle = style
        singleRenderer.isFillPoints = fill
        renderer.addSeriesRenderer(singleRenderer)

        for (i in 0 until renderer.seriesRendererCount) {
            renderer.getSeriesRendererAt(i).isDisplayChartValues = true
            renderer.getSeriesRendererAt(i).chartValuesTextSize = (25).toFloat()
        }

        renderer.chartTitle = title // 折線圖名稱
        renderer.chartTitleTextSize = (45).toFloat() // 折線圖名稱字形大小
        renderer.labelsTextSize = (30).toFloat() // 設置軸標籤文本大小
        renderer.axisTitleTextSize = (35).toFloat()// 設置坐標軸標題文本大小
        renderer.xTitle = xTitle // X軸名稱
        renderer.yTitle = yTitle // Y軸名稱
        renderer.legendHeight = 100
        renderer.labelsTextSize = (35).toFloat()
        renderer.xAxisMin = xMin // X軸顯示最小值
        renderer.xAxisMax = xMax // X軸顯示最大值
        renderer.xLabelsColor = Color.BLACK // X軸線顏色
        renderer.yAxisMin = yMin // Y軸顯示最小值
        renderer.yAxisMax = yMax // Y軸顯示最大值
        renderer.axesColor = axisColor // 設定坐標軸顏色
        renderer.setYLabelsColor(0, Color.BLACK) // Y軸線顏色
        renderer.labelsColor = Color.BLACK // 設定標籤顏色
        renderer.marginsColor = Color.WHITE // 設定背景顏色
        renderer.setShowGrid(true) // 設定格線
        renderer.margins = intArrayOf(30, 50, 0, 20)

        return renderer
    }
}