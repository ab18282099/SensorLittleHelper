package com.example.user.siolSupervise.fragments

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.user.siolSupervise.models.AppSettingModel
import com.example.user.siolSupervise.R
import com.example.user.siolSupervise.ui.recyclerView.settingRecycler.SettingAdapter
import com.example.user.siolSupervise.ui.dialog.SensorSettingDialog
import kotlinx.android.synthetic.main.fragment_setting.*
import org.jetbrains.anko.toast
import org.jetbrains.anko.vibrator

class SettingFragment : BaseFragment(), FragmentBackPressedListener {
    companion object {
        fun newInstance(): SettingFragment {
            val fragment = SettingFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _mainSettingDataTemp = arrayOfNulls<String>(4)
    private var _mainSettingDataText = arrayOfNulls<String>(5)
    private var _recyclerSetting: RecyclerView? = null
    private var _mAdapter: SettingAdapter? = null
    private var _appSettingModel: AppSettingModel? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_setting, container, false)
        _recyclerSetting = view.findViewById(R.id._recyclerSetting)

        _appSettingModel = AppSettingModel(activity)

        _mainSettingDataText = arrayOf("1.ESP8266之IP位址 ", "2.ESP8266之通訊埠 ", "3.Server IP位置 ",
                "4.歷史資料儲存檔名 ", "5.AUTO TOGGLE ")
        _mainSettingDataTemp = arrayOf(_appSettingModel!!.wifiIp(), _appSettingModel!!.wifiPort(),
                _appSettingModel!!.serverIp(), _appSettingModel!!.fileSavedName())

        val layoutManger = LinearLayoutManager(activity)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerSetting!!.layoutManager = layoutManger

        _mAdapter = SettingAdapter(activity, _mainSettingDataTemp, _mainSettingDataText)
        _recyclerSetting!!.adapter = _mAdapter

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_sensor_setting.text = getString(R.string.sensor_setting)
        btn_sensor_setting.setOnClickListener {
            val dialog = SensorSettingDialog(activity)
            dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.show()
        }

        btn_done_setting.text = getString(R.string.done)
        btn_done_setting.setOnClickListener {
            doneSetting()
        }
    }

    override fun onFragmentBackPressed() {
        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain)
        vpMain.currentItem = 1
    }

    private fun doneSetting() {
        val regIPAddress = Regex("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$")
        val regPort = Regex("([0-9][0-9])|([0-9][0-9][0-9])")

        if (_mainSettingDataTemp[0]!!.matches(regIPAddress) && _mainSettingDataTemp[1]!!.matches(regPort) &&
                _mainSettingDataTemp[2]!!.matches(regIPAddress) && _mainSettingDataTemp[3] != "") {
            toast("設定成功")
            _appSettingModel!!.putString("wifiIp", _mainSettingDataTemp[0])
            _appSettingModel!!.putString("wifiPort", _mainSettingDataTemp[1])
            _appSettingModel!!.putString("serverIp", _mainSettingDataTemp[2])
            _appSettingModel!!.putString("fileSavedName", _mainSettingDataTemp[3])
        }
        else {
            val v = activity.vibrator
            v.vibrate(500)
            toast("設定失敗")
        }
    }
}