package com.example.user.soil_supervise_kotlin.Fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.user.soil_supervise_kotlin.DbDataDownload.HttpHelper
import com.example.user.soil_supervise_kotlin.DbDataDownload.IHttpAction
import com.example.user.soil_supervise_kotlin.Ui.FragmentBackPressedListener
import com.example.user.soil_supervise_kotlin.Utility.*
import com.example.user.soil_supervise_kotlin.R
import com.example.user.soil_supervise_kotlin.Ui.CustomerProgressDialog
import kotlinx.android.synthetic.main.fragment_chart.*
import org.achartengine.ChartFactory
import org.achartengine.chart.PointStyle
import org.achartengine.model.TimeSeries
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.renderer.XYSeriesRenderer
import org.apache.commons.beanutils.ConvertUtils
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChartFragment : BaseFragment(), FragmentBackPressedListener
{
    companion object
    {
        fun NewInstance(): ChartFragment
        {
            val fragment = ChartFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _sharePref: MySharedPreferences? = null

    private var _mTestDialog: CustomerProgressDialog? = null
    private var _buildChartHelper: HttpHelper? = null

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        Log.e("ChartFragment", "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.e("ChartFragment", "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater!!.inflate(R.layout.fragment_chart, container, false)
        _sharePref = MySharedPreferences.InitInstance(activity)
        Log.e("ChartFragment", "onCreateView")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        Log.e("ChartFragment", "onActivityCreated")
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        Log.e("ChartFragment", "onViewCreated")

        btn_chart_dialog.text = "選擇感測器"
        btn_chart_dialog.setOnClickListener {
            val setChartDialog = SetChartDialog(activity)
            setChartDialog.show()
            setChartDialog.setCanceledOnTouchOutside(true)
        }

        btn_chart_dialog.setOnLongClickListener {
            _mTestDialog = CustomerProgressDialog(context, "TestDialog")
            _mTestDialog!!.setOnBackPressedListener { toast("This is test") }
            _mTestDialog!!.show()

            true
        }
    }

    override fun onStart()
    {
        super.onStart()
        Log.e("ChartFragment", "onStart")
    }

    override fun onResume()
    {
        super.onResume()
        Log.e("ChartFragment", "onResume")
    }

    override fun onPause()
    {
        super.onPause()
        Log.e("ChartFragment", "onPause")
    }

    override fun onStop()
    {
        super.onStop()
        Log.e("ChartFragment", "onStop")
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        Log.e("ChartFragment", "onDestroyView")
    }

    override fun onDestroy()
    {
        super.onDestroy()
        Log.e("ChartFragment", "onDestroy")
    }

    override fun onDetach()
    {
        super.onDetach()
        Log.e("ChartFragment", "onDetach")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean)
    {
        super.setUserVisibleHint(isVisibleToUser)
        Log.e("ChartFragment", isVisibleToUser.toString())
    }

    override fun OnFragmentBackPressed()
    {
        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager
        vpMain.currentItem = 1
    }

    private fun SetChartDialog(context: Context): AlertDialog
    {
        val nullParent: ViewGroup? = null
        val factory = LayoutInflater.from(context)
        val convertView = factory.inflate(R.layout.dialog_set_chart, nullParent)

        val spinner_chart = convertView.findViewById<Spinner>(R.id.spinner_chart)
        val btn_set_chart = convertView.findViewById<Button>(R.id.btn_set_chart)

        val dialog = android.app.AlertDialog.Builder(context).setView(convertView)
                .create()

        var chartId = 0
        val sensorQuantity = _sharePref!!.GetSensorQuantity()
        val sensorTitleList = arrayOfNulls<String>(sensorQuantity)
        for (i in 0 until sensorQuantity)
        {
            sensorTitleList[i] = _sharePref!!.GetSensorName(i)
        }

        val sensorListAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, sensorTitleList)
        spinner_chart.adapter = sensorListAdapter

        spinner_chart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long)
            {
                chartId = position
            }

            override fun onNothingSelected(adapterView: AdapterView<*>)
            {
            }
        }

        btn_set_chart.setOnClickListener {
            dialog.dismiss()
            SetChartView(chartId)
        }

        // DO NOT USE getApplicationContext!!!
        return dialog
    }

    private fun SetChartView(chartID: Int)
    {
        tx_chartTitle.text = _sharePref!!.GetSensorName(chartID)
        val serverIP = _sharePref!!.GetServerIP()
        val user = _sharePref!!.GetUsername()
        val pass = _sharePref!!.GetPassword()
        _buildChartHelper = HttpHelper.InitInstance(context)
        _buildChartHelper!!.SetHttpAction(object : IHttpAction
        {
            override fun OnHttpRequest()
            {
                val phpAddress = "http://$serverIP/android_mysql.php?&server=$serverIP&user=$user&pass=$pass"
                ChartDataRenew(HttpRequest.DownloadFromMySQL("society", phpAddress), chartID)
            }

            override fun OnException(e: Exception)
            {
                Log.e("ChartDownloadFailed", e.toString())
                runOnUiThread { toast(e.toString()) }
            }

            override fun OnPostExecute()
            {
            }
        })
        _buildChartHelper!!.StartHttpThread()
    }

    private fun ChartDataRenew(jsonString: String?, chartID: Int)
    {
        val title: String
        val interval: Int

        try
        {
            val jsonArray = JSONArray(jsonString)

            if (jsonArray.length() >= 10000) interval = 1000
            else if (jsonArray.length() in 5000 until 10000) interval = 99
            else if (jsonArray.length() in 1000 until 5000) interval = 49
            else if (jsonArray.length() in 500 until 1000) interval = 9
            else interval = 1

            val x = ArrayList<kotlin.Array<Date?>>()
            val y = ArrayList<DoubleArray>()

            val sensorData = arrayOfNulls<String>(jsonArray.length())

            val timeData = arrayOfNulls<Date>(jsonArray.length())

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) //HH: 24hr / hh: 12hr

            for (i in 0 until jsonArray.length())
            {
                try
                {
                    timeData[i] = simpleDateFormat.parse(jsonArray.getJSONObject(i).getString("time"))
                }
                catch (e: Exception)
                {
                    Log.e("ParseDate", e.toString())
                }
            }

            x.add(timeData)
            val firstTime = timeData[0]!!.time.toDouble()
            val lastTime = timeData[timeData.size - 1]!!.time.toDouble()

            for (i in 0 until jsonArray.length())
            {
                sensorData[i] = jsonArray.getJSONObject(i).getString("sensor_" + (chartID + 1).toString())
            }

            val yData = ConvertUtils.convert(sensorData, java.lang.Double.TYPE) as DoubleArray
            y.add(yData)
            title = _sharePref!!.GetSensorName(chartID)

            val dataSet = BuildDataSet(title, x, y, interval)
            val renderer = BuildRenderer(Color.BLUE, PointStyle.CIRCLE, true)
            InitChartSetting(renderer, "數據折線圖", "TIME", "%", firstTime, lastTime, (-50).toDouble(), (100).toDouble(), Color.BLACK)

            val seriesLength = renderer.seriesRendererCount

            for (i in 0 until seriesLength)
            {
                val seriesRenderer = renderer.getSeriesRendererAt(i)
                seriesRenderer.isDisplayChartValues = true
                seriesRenderer.chartValuesTextSize = (25).toFloat()
            }

            runOnUiThread {
                chartLayout.removeAllViewsInLayout()
                val chart = ChartFactory.getTimeChartView(activity, dataSet, renderer, null)
                chart.setOnTouchListener { view, motionEvent ->
                    chart.parent.requestDisallowInterceptTouchEvent(true)
                    false
                }

                chartLayout.addView(chart)
            }
        }
        catch (e: Exception)
        {
            Log.e("AnalysisDataFailed", e.toString())
        }
    }

    private fun BuildDataSet(title: String, xValue: ArrayList<kotlin.Array<Date?>>,
                             yVale: ArrayList<DoubleArray>, interval: Int): XYMultipleSeriesDataset
    {
        val dataSet = XYMultipleSeriesDataset()

        val series = TimeSeries(title)

        val xV = xValue.get(0)
        val yV = yVale.get(0)

        for (i in 0 until xV.size step interval)
        {
            series.add(xV[i], yV[i])
        }
        dataSet.addSeries(series)
        return dataSet
    }

    private fun BuildRenderer(color: Int, style: PointStyle, fill: Boolean): XYMultipleSeriesRenderer
    {
        val renderer = XYMultipleSeriesRenderer()

        val singleRenderer = XYSeriesRenderer()
        singleRenderer.color = color
        singleRenderer.pointStyle = style
        singleRenderer.isFillPoints = fill
        renderer.addSeriesRenderer(singleRenderer)

        return renderer
    }

    private fun InitChartSetting(renderer: XYMultipleSeriesRenderer, title: String, xTitle: String,
                                 yTitle: String, xMin: Double, xMax: Double,
                                 yMin: Double, yMax: Double, axisColor: Int)
    {
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
    }
}