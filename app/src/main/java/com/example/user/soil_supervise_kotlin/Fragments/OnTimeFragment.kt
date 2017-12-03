package com.example.user.soil_supervise_kotlin.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.android.volley.*
import com.example.user.soil_supervise_kotlin.MySqlDb.DbAction
import com.example.user.soil_supervise_kotlin.MySqlDb.IDbResponse
import com.example.user.soil_supervise_kotlin.MySqlDb.HttpHelper
import com.example.user.soil_supervise_kotlin.MySqlDb.IHttpAction
import com.example.user.soil_supervise_kotlin.Utility.HttpRequest
import com.example.user.soil_supervise_kotlin.Ui.ProgressDialog
import com.example.user.soil_supervise_kotlin.Model.AppSettingModel
import com.example.user.soil_supervise_kotlin.R
import com.example.user.soil_supervise_kotlin.Ui.RecyclerView.OnTimeAdapter
import com.example.user.soil_supervise_kotlin.Ui.RecyclerView.SimpleDividerItemDecoration
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

    private var _sensorDataList = ArrayList<String?>()
    private var _sensorQuantity = 5
    private var _count = 5
    private var _countHandler: Handler? = null
    private var _countRunnable: Runnable? = null
    private var _recyclerOnTime: RecyclerView? = null
    private var _appSettingModel: AppSettingModel? = null
    private var _wifiToggleHelper : HttpHelper? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater!!.inflate(R.layout.fragment_on_time, container, false)
        _appSettingModel = AppSettingModel(activity)
        _recyclerOnTime = view.findViewById<RecyclerView>(R.id.recycler_on_time) as RecyclerView
        val layoutManger = LinearLayoutManager(activity)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerOnTime?.layoutManager = layoutManger

        return view
    }

    override fun OnFragmentBackPressed()
    {
        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager
        vpMain.currentItem = 1
    }

    fun SetSensorQuantity(quantity: Int)
    {
        _sensorQuantity = quantity
    }

    fun TryLoadLastData(user: String, pass: String)
    {
        _sensorDataList.clear()

        val ServerIP = _appSettingModel!!.ServerIp()
        val phpAddress = "http://$ServerIP/android_mysql_last.php?&server=$ServerIP&user=$user&pass=$pass"
        val refreshDataAction = DbAction(activity)
        refreshDataAction.SetResponse(object : IDbResponse
        {
            override fun OnSuccess(jsonObject: JSONObject)
            {
                val sensorQuantity = _appSettingModel!!.SensorQuantity()
                val sensorData = arrayOfNulls<String>(sensorQuantity + 2)

                for (i in 0 until sensorQuantity + 2)
                {
                    when (i)
                    {
                        0 -> sensorData[i] = jsonObject.getString("ID")
                        sensorQuantity + 1 -> sensorData[i] = jsonObject.getString("time")
                        else -> sensorData[i] = jsonObject.getString("sensor_" + (i).toString())
                    }
                }

                _sensorDataList.addAll(sensorData)
                _recyclerOnTime?.adapter = OnTimeAdapter(activity, _sensorDataList)
                _recyclerOnTime?.addItemDecoration(SimpleDividerItemDecoration(activity))
                toast("連接成功")
                WarningFunction(_sensorDataList)
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
        refreshDataAction.DoDbOperate(phpAddress)
    }

    private fun WarningFunction(sensorDataList: ArrayList<String?>)
    {
        val toggleSensorList = ArrayList<String>()
        val toggleSensorPinList = ArrayList<String>()
        val toggleText: String
        val togglePin: String

        for (i in 1.._sensorQuantity)
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
                    val autoDialog = AutoToggle(togglePin, "發現狀態改變！")
                    autoDialog.show()
                    autoDialog.setCancelable(false)
                }
                false ->
                {
                    alert("警告！") {
                        message = toggleText + "數值狀態改變！是否移至Wi-Fi遙控頁面?"
                        yesButton {
                            val vpMain = activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager
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

    private fun AutoToggle(togglePin: String, message: String): AlertDialog
    {
        val nullParent: ViewGroup? = null
        val convertView = LayoutInflater.from(activity).inflate(R.layout.dialog_count_down, nullParent)

        val tx_count = convertView.findViewById<TextView>(R.id.tx_count) as TextView
        val btn_cancel = convertView.findViewById<Button>(R.id.btn_cancel) as Button

        val dialog = android.app.AlertDialog.Builder(activity).setView(convertView).create()

        _countHandler = Handler()
        _countRunnable = Runnable {
            if (_count < 0)
            {
                dialog.dismiss()
                _count = 5
                _countHandler!!.removeCallbacks(_countRunnable)

                val ipAddress = _appSettingModel!!.WifiIp()
                val port = _appSettingModel!!.WifiPort()
                TryTogglePin(ipAddress, port, togglePin)
            }
            else
            {
                _count -= 1
                tx_count.text = getString(R.string.countDown, message, (_count + 1).toString(), togglePin)
                _countHandler!!.postDelayed(_countRunnable, 1000)
            }
        }
        _countHandler!!.post(_countRunnable)

        btn_cancel.text = getString(R.string.cancel)
        btn_cancel.setOnClickListener {
            dialog.dismiss()
            _count = 5
            _countHandler!!.removeCallbacks(_countRunnable)
        }

        return dialog
    }

    private fun TryTogglePin(ipAddress: String, port: String, parameterValue: String)
    {
        _wifiToggleHelper = HttpHelper.InitInstance(activity)
        _wifiToggleHelper!!.SetHttpAction(object : IHttpAction
        {
            override fun OnHttpRequest()
            {
                val requestReply = HttpRequest.SendToggleRequest(parameterValue, ipAddress, port, "pin")
                val resultDialog = ProgressDialog.DialogProgress(activity, requestReply, View.GONE)
                resultDialog.show()

                val jsonResult = JSONObject(requestReply)

                for (i in 0 until _sensorQuantity)
                {
                    _appSettingModel!!.PutString("getPin" + i.toString() + "State", jsonResult.getString("PIN" + _appSettingModel!!.SensorPin(i)))
                }
            }

            override fun OnException(e: Exception)
            {
                Log.e("Toggling Pin in OnTimeFragment", e.toString())
            }

            override fun OnPostExecute()
            {
            }
        })
        _wifiToggleHelper!!.StartHttpThread()
    }
}