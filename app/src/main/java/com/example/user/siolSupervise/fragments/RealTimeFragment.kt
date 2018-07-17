package com.example.user.siolSupervise.fragments

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.*
import com.example.user.siolSupervise.db.DbAction
import com.example.user.siolSupervise.db.IDbResponse
import com.example.user.siolSupervise.models.AppSettingModel
import com.example.user.siolSupervise.dto.PhpUrlDto
import com.example.user.siolSupervise.R
import com.example.user.siolSupervise.ui.dialog.AutoToggleDialog
import com.example.user.siolSupervise.ui.recyclerView.OnTimeAdapter
import com.example.user.siolSupervise.ui.recyclerView.SimpleDividerItemDecoration
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.json.JSONObject

class RealTimeFragment : BaseFragment(), FragmentBackPressedListener {
    companion object {
        fun newInstance(): RealTimeFragment {
            val fragment = RealTimeFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _recyclerOnTime: RecyclerView? = null
    private var _appSettingModel: AppSettingModel? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_on_time, container, false)
        _appSettingModel = AppSettingModel(activity)
        _recyclerOnTime = view.findViewById(R.id.recycler_on_time)
        val layoutManger = LinearLayoutManager(activity)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerOnTime?.layoutManager = layoutManger

        return view
    }

    override fun onFragmentBackPressed() {
        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain)
        vpMain.currentItem = 1
    }

    fun tryLoadLastData() {
        val refreshDataAction = DbAction(activity)
        refreshDataAction.setResponse(object : IDbResponse {
            override fun onSuccess(jsonObject: JSONObject) {
                val sensorData = ArrayList<String?>()

                for (i in 0 until _appSettingModel!!.sensorQuantity() + 2) {
                    when (i) {
                        0 -> sensorData.add(jsonObject.getString("ID"))
                        _appSettingModel!!.sensorQuantity() + 1 -> sensorData.add(jsonObject.getString("time"))
                        else -> sensorData.add(jsonObject.getString("sensor_" + (i).toString()))
                    }
                }

                _recyclerOnTime?.adapter = OnTimeAdapter(activity, sensorData)
                _recyclerOnTime?.addItemDecoration(SimpleDividerItemDecoration(activity))
                toast("連接成功")
                warningFunction(sensorData)
            }

            override fun onException(e: Exception) {
                Log.e("loadOnTimeData", e.toString())
                toast(e.toString())
            }

            override fun onError(volleyError: VolleyError) {
                VolleyLog.e("ERROR", volleyError.toString())
                toast("CONNECT ERROR")
            }
        })
        refreshDataAction.doDbOperate(PhpUrlDto(activity).LoadingLastData)
    }

    private fun warningFunction(sensorDataList: ArrayList<String?>) {
        val toggleSensorList = ArrayList<String>()
        val toggleSensorPinList = ArrayList<String>()
        val toggleText: String
        val togglePin: String

        for (i in 1.._appSettingModel!!.sensorQuantity()) {
            val warnCondition = _appSettingModel!!.warningCondition(i - 1).toFloat()
            val sensorDataFloat = if (sensorDataList[i] == "") (-1).toFloat() else sensorDataList[i]!!.toFloat()

            if (_appSettingModel!!.sensorVisibility(i - 1) == View.VISIBLE) {
                if (isWarning(sensorDataFloat, warnCondition)) {
                    if (_appSettingModel!!.pinState(i - 1) == "OFF") {
                        toggleSensorList.add(_appSettingModel!!.sensorName(i - 1))
                        toggleSensorPinList.add(_appSettingModel!!.sensorPin(i - 1))
                    }
                }
                else {
                    if (_appSettingModel!!.pinState(i - 1) == "ON") {
                        toggleSensorList.add(_appSettingModel!!.sensorName(i - 1))
                        toggleSensorPinList.add(_appSettingModel!!.sensorPin(i - 1))
                    }
                }
            }
        }

        if (toggleSensorList.isNotEmpty() && toggleSensorPinList.isNotEmpty()) {
            toggleText = toggleSensorList.joinToString(separator = ", ")
            togglePin = toggleSensorPinList.joinToString(separator = ",")

            when (_appSettingModel!!.isAutoToggle()) {
                true -> {
                    val autoDialog = AutoToggleDialog(activity, togglePin, "發現狀態改變！")
                    autoDialog.show()
                    autoDialog.setCancelable(false)
                }
                false -> {
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

    private fun isWarning(sensorData: Float?, warnConditional: Float?): Boolean {
        val invalidFloat = (-1).toFloat()

        if (sensorData != null && warnConditional != null && sensorData != invalidFloat && sensorData < warnConditional)
            return true

        return false
    }
}