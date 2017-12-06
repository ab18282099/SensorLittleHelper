package com.example.user.soil_supervise_kotlin.fragments

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.*
import com.example.user.soil_supervise_kotlin.db.DbAction
import com.example.user.soil_supervise_kotlin.db.IDbResponse
import com.example.user.soil_supervise_kotlin.models.AppSettingModel
import com.example.user.soil_supervise_kotlin.dto.PhpUrlDto
import com.example.user.soil_supervise_kotlin.R
import com.example.user.soil_supervise_kotlin.ui.dialog.AutoToggleDialog
import com.example.user.soil_supervise_kotlin.ui.recyclerView.OnTimeAdapter
import com.example.user.soil_supervise_kotlin.ui.recyclerView.SimpleDividerItemDecoration
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.json.JSONObject

class OnTimeFragment : BaseFragment(), FragmentBackPressedListener
{
    companion object
    {
        fun NewInstance(): OnTimeFragment
        {
            val fragment = OnTimeFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _recyclerOnTime: RecyclerView? = null
    private var _appSettingModel: AppSettingModel? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater!!.inflate(R.layout.fragment_on_time, container, false)
        _appSettingModel = AppSettingModel(activity)
        _recyclerOnTime = view.findViewById(R.id.recycler_on_time)
        val layoutManger = LinearLayoutManager(activity)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerOnTime?.layoutManager = layoutManger

        return view
    }

    override fun OnFragmentBackPressed()
    {
        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain)
        vpMain.currentItem = 1
    }

    fun TryLoadLastData()
    {
        val refreshDataAction = DbAction(activity)
        refreshDataAction.SetResponse(object : IDbResponse
        {
            override fun OnSuccess(jsonObject: JSONObject)
            {
                val sensorData = ArrayList<String?>()

                for (i in 0 until _appSettingModel!!.SensorQuantity() + 2)
                {
                    when (i)
                    {
                        0 -> sensorData.add(jsonObject.getString("ID"))
                        _appSettingModel!!.SensorQuantity() + 1 -> sensorData.add(jsonObject.getString("time"))
                        else -> sensorData.add(jsonObject.getString("sensor_" + (i).toString()))
                    }
                }

                _recyclerOnTime?.adapter = OnTimeAdapter(activity, sensorData)
                _recyclerOnTime?.addItemDecoration(SimpleDividerItemDecoration(activity))
                toast("連接成功")
                WarningFunction(sensorData)
            }

            override fun OnException(e: Exception)
            {
                Log.e("LoadOnTimeData", e.toString())
                toast(e.toString())
            }

            override fun OnError(volleyError: VolleyError)
            {
                VolleyLog.e("ERROR", volleyError.toString())
                toast("CONNECT ERROR")
            }
        })
        refreshDataAction.DoDbOperate(PhpUrlDto(activity).LoadingLastData)
    }

    private fun WarningFunction(sensorDataList: ArrayList<String?>)
    {
        val toggleSensorList = ArrayList<String>()
        val toggleSensorPinList = ArrayList<String>()
        val toggleText: String
        val togglePin: String

        for (i in 1.._appSettingModel!!.SensorQuantity())
        {
            val warnCondition = _appSettingModel!!.WarningCondition(i - 1).toFloat()
            val sensorDataFloat = if (sensorDataList[i] == "") (-1).toFloat() else sensorDataList[i]!!.toFloat()

            if (_appSettingModel!!.SensorVisibility(i - 1) == View.VISIBLE)
            {
                if (IsWarning(sensorDataFloat, warnCondition))
                {
                    if (_appSettingModel!!.PinState(i - 1) == "OFF")
                    {
                        toggleSensorList.add(_appSettingModel!!.SensorName(i - 1))
                        toggleSensorPinList.add(_appSettingModel!!.SensorPin(i - 1))
                    }
                }
                else
                {
                    if (_appSettingModel!!.PinState(i - 1) == "ON")
                    {
                        toggleSensorList.add(_appSettingModel!!.SensorName(i - 1))
                        toggleSensorPinList.add(_appSettingModel!!.SensorPin(i - 1))
                    }
                }
            }
        }

        if (toggleSensorList.isNotEmpty() && toggleSensorPinList.isNotEmpty())
        {
            toggleText = toggleSensorList.joinToString(separator = ", ")
            togglePin = toggleSensorPinList.joinToString(separator = ",")

            when(_appSettingModel!!.IsAutoToggle())
            {
                true ->
                {
                    val autoDialog = AutoToggleDialog(activity, togglePin, "發現狀態改變！")
                    autoDialog.show()
                    autoDialog.setCancelable(false)
                }
                false ->
                {
                    alert("警告！") {
                        message = toggleText + "數值狀態改變！是否移至Wi-Fi遙控頁面?"
                        yesButton {
                            val vpMain = activity.findViewById<ViewPager>(R.id._vpMain)
                            vpMain.currentItem = 2
                        }
                        noButton { }
                    }.show()
                }
            }
        }
    }

    private fun IsWarning(sensorData: Float?, warnConditional: Float?): Boolean
    {
        val invalidFloat = (-1).toFloat()

        if (sensorData != null && warnConditional != null && sensorData != invalidFloat && sensorData < warnConditional)
            return true

        return false
    }
}