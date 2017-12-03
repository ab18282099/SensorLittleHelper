package com.example.user.soil_supervise_kotlin.Fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.android.volley.*
import com.example.user.soil_supervise_kotlin.MySqlDb.DbAction
import com.example.user.soil_supervise_kotlin.MySqlDb.IDbResponse
import com.example.user.soil_supervise_kotlin.Utility.MySharedPreferences
import com.example.user.soil_supervise_kotlin.R
import com.example.user.soil_supervise_kotlin.Ui.RecyclerView.SettingRecycler.SensorDialogAdapter
import com.example.user.soil_supervise_kotlin.Ui.RecyclerView.SettingRecycler.SettingAdapter
import com.example.user.soil_supervise_kotlin.Ui.RecyclerView.SimpleDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_setting.*
import org.jetbrains.anko.toast
import org.jetbrains.anko.vibrator
import org.json.JSONObject

class SettingFragment : BaseFragment(), FragmentBackPressedListener
{
    companion object
    {
        fun NewInstance(): SettingFragment
        {
            val fragment = SettingFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _mainSettingDataTemp = arrayOfNulls<String>(4)
    private var _mainSettingDataText = arrayOfNulls<String>(5)
    private var _recyclerSetting: RecyclerView? = null
    private var _mAdapter : SettingAdapter? = null
    private var _sharePref: MySharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater!!.inflate(R.layout.fragment_setting, container, false)
        _recyclerSetting = view.findViewById<RecyclerView>(R.id._recyclerSetting) as RecyclerView

        _sharePref = MySharedPreferences.InitInstance(activity)

        _mainSettingDataText = arrayOf("1.ESP8266之IP位址 ", "2.ESP8266之通訊埠 ", "3.Server IP位置 ",
                "4.歷史資料儲存檔名 ", "5.AUTO TOGGLE ")
        _mainSettingDataTemp = arrayOf(_sharePref!!.GetIPAddress(), _sharePref!!.GetPort(),
                _sharePref!!.GetServerIP(), _sharePref!!.GetFileSavedName())

        val layoutManger = LinearLayoutManager(this.context)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerSetting!!.layoutManager = layoutManger

        _mAdapter = SettingAdapter(context, _mainSettingDataTemp, _mainSettingDataText)
        _recyclerSetting!!.adapter = _mAdapter

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        btn_sensor_setting.text = getString(R.string.sensor_setting)
        btn_sensor_setting.setOnClickListener {
            val dialog = DialogSensorSetter(activity)
            dialog.show()
            dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.setCanceledOnTouchOutside(true)
        }

        btn_done_setting.text = getString(R.string.done)
        btn_done_setting.setOnClickListener {
            DoneSetting()
        }
    }

    override fun OnFragmentBackPressed()
    {
        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager
        vpMain.currentItem = 1
    }

    private fun DoneSetting()
    {
        val regIPAddress = Regex("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$")
        val regPort = Regex("([0-9][0-9])|([0-9][0-9][0-9])")

        if (_mainSettingDataTemp[0]!!.matches(regIPAddress) && _mainSettingDataTemp[1]!!.matches(regPort) &&
                _mainSettingDataTemp[2]!!.matches(regIPAddress) && _mainSettingDataTemp[3] != "")
        {
            toast("設定成功")
            _sharePref!!.PutString("GetIPAddress", _mainSettingDataTemp[0])
            _sharePref!!.PutString("GetPort", _mainSettingDataTemp[1])
            _sharePref!!.PutString("GetServerIP", _mainSettingDataTemp[2])
            _sharePref!!.PutString("GetFileSavedName", _mainSettingDataTemp[3])
        }
        else
        {
            val v = context.vibrator
            v.vibrate(500)
            toast("設定失敗")
        }
    }

    private fun DialogSensorSetter(context: Context): AlertDialog
    {
        val nullParent: ViewGroup? = null
        val convertView = LayoutInflater.from(context).inflate(R.layout.dialog_sensor_setting, nullParent)

        val recycler_in_dialog = convertView.findViewById<RecyclerView>(R.id.recycler_in_dialog) as RecyclerView
        val btn_insert = convertView.findViewById<Button>(R.id.btn_insert) as Button
        val btn_drop = convertView.findViewById<Button>(R.id.btn_drop) as Button
        val btn_sensor_done = convertView.findViewById<Button>(R.id.btn_sensor_done) as Button

        val dialog = android.app.AlertDialog.Builder(context).setView(convertView).create()

        val layoutManger = LinearLayoutManager(context)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        recycler_in_dialog.layoutManager = layoutManger

        val dialogAdapter = SensorDialogAdapter(context)
        recycler_in_dialog.adapter = dialogAdapter
        val mDividerLine = SimpleDividerItemDecoration(context)
        recycler_in_dialog.addItemDecoration(mDividerLine)

        btn_insert.setOnClickListener {
            val id = dialogAdapter.itemCount
            TryEditSensorQuantity("create_sensor", (id + 1).toString(), dialogAdapter)
        }
        btn_drop.setOnClickListener {
            if (_sharePref!!.GetSensorQuantity() == 5)
            {
                toast("At least 5 sensor")
            }
            else
            {
                val id = dialogAdapter.itemCount
                TryEditSensorQuantity("delete_sensor", id.toString(), dialogAdapter)
            }
        }
        btn_sensor_done.setOnClickListener {
            val wrongInput = CheckInputType()

            if (wrongInput == -1)
            {
                toast("設定成功")
                dialog.dismiss()
            }
            else
            {
                val v = context.vibrator
                v.vibrate(500)
                toast("(" + _sharePref!!.GetSensorName(wrongInput) + ")" + "輸入有誤")
            }
        }

        return dialog
    }

    private fun TryEditSensorQuantity(query: String, sensorID: String, dialogAdapter: SensorDialogAdapter)
    {
        val ServerIP = _sharePref!!.GetServerIP()
        val user = _sharePref!!.GetUsername()
        val pass = _sharePref!!.GetPassword()
        val phpAddress = "http://$ServerIP/$query.php?&server=$ServerIP&user=$user&pass=$pass&sensor_id=$sensorID"
        val addSensorAction = DbAction(context)
        addSensorAction.SetResponse(object : IDbResponse
        {
            override fun OnSuccess(jsonObject: JSONObject)
            {
                val success = jsonObject.getInt("success")
                val message = jsonObject.getString("message")

                if (success == 1 && message == "Created Successfully.")
                {
                    dialogAdapter.notifyItemInserted(_sharePref!!.GetSensorQuantity())
                    _sharePref!!.PutInt("GetSensorQuantity", _sharePref!!.GetSensorQuantity() + 1)
                    toast("已新增Sensor")
                }
                else if (success == 1 && message == "Deleted Successfully.")
                {
                    dialogAdapter.notifyItemRemoved(_sharePref!!.GetSensorQuantity() - 1)
                    _sharePref!!.PutInt("GetSensorQuantity", _sharePref!!.GetSensorQuantity() - 1)
                    toast("已移除Sensor")
                }
                else
                {
                    toast("操作失敗")
                }
            }

            override fun OnException(e: Exception)
            {
                Log.e("Editing db", e.toString())
            }

            override fun OnError(volleyError: VolleyError)
            {
                VolleyLog.e("ERROR", volleyError.toString())
                toast("CONNECT ERROR")
            }
        })
        addSensorAction.DoDbOperate(phpAddress)
    }

    private fun CheckInputType(): Int
    {
        val regWarningCondition = Regex("[0-9]*.?[0-9]+")
        val regPin = Regex("[0-9]{1,2}")
        val sensorQuantity = _sharePref!!.GetSensorQuantity()

        for (i in 0 until sensorQuantity)
        {
            val warnCondition = _sharePref!!.GetSensorCondition(i)
            val pin = _sharePref!!.GetSensorPin(i)

            if (!warnCondition.matches(regWarningCondition) || !pin.matches(regPin))
            {
                return i
            }
        }

        return -1
    }
}