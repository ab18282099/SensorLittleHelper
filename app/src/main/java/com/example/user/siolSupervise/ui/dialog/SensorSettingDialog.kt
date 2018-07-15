package com.example.user.siolSupervise.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import com.android.volley.VolleyError
import com.android.volley.VolleyLog
import com.example.user.siolSupervise.R
import com.example.user.siolSupervise.db.DbAction
import com.example.user.siolSupervise.db.IDbResponse
import com.example.user.siolSupervise.dto.PhpUrlDto
import com.example.user.siolSupervise.models.AppSettingModel
import com.example.user.siolSupervise.ui.recyclerView.settingRecycler.SensorDialogAdapter
import com.example.user.siolSupervise.ui.recyclerView.SimpleDividerItemDecoration
import org.jetbrains.anko.toast
import org.jetbrains.anko.vibrator
import org.json.JSONObject

class SensorSettingDialog constructor(context: Context) : AlertDialog(context) {
    private val _context = context
    private val _appSettingModel = AppSettingModel(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_sensor_setting)
        val recycler_in_dialog = findViewById<RecyclerView>(R.id.recycler_in_dialog)
        val btn_insert = findViewById<Button>(R.id.btn_insert)
        val btn_drop = findViewById<Button>(R.id.btn_drop)
        val btn_sensor_done = findViewById<Button>(R.id.btn_sensor_done)
        val layoutManger = LinearLayoutManager(_context)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        recycler_in_dialog.layoutManager = layoutManger

        val dialogAdapter = SensorDialogAdapter(_context)
        recycler_in_dialog.adapter = dialogAdapter
        val mDividerLine = SimpleDividerItemDecoration(_context)
        recycler_in_dialog.addItemDecoration(mDividerLine)

        btn_insert.setOnClickListener {
            val id = dialogAdapter.itemCount
            tryEditSensorQuantity("create_sensor", (id + 1).toString(), dialogAdapter)
        }
        btn_drop.setOnClickListener {
            if (_appSettingModel.sensorQuantity() == 5) {
                _context.toast("At least 5 sensor")
            }
            else {
                val id = dialogAdapter.itemCount
                tryEditSensorQuantity("delete_sensor", id.toString(), dialogAdapter)
            }
        }
        btn_sensor_done.setOnClickListener {
            val wrongInput = checkInputType()

            if (wrongInput == -1) {
                _context.toast("設定成功")
                this.dismiss()
            }
            else {
                val v = _context.vibrator
                v.vibrate(500)
                _context.toast("(" + _appSettingModel.sensorName(wrongInput) + ")" + "輸入有誤")
            }
        }
    }

    private fun tryEditSensorQuantity(query: String, sensorId: String, dialogAdapter: SensorDialogAdapter) {
        val addSensorAction = DbAction(_context)
        addSensorAction.setResponse(object : IDbResponse {
            override fun onSuccess(jsonObject: JSONObject) {
                val success = jsonObject.getInt("success")
                val message = jsonObject.getString("message")

                if (success == 1 && message == "Created Successfully.") {
                    dialogAdapter.notifyItemInserted(_appSettingModel.sensorQuantity())
                    _appSettingModel.putInt("sensorQuantity", _appSettingModel.sensorQuantity() + 1)
                    _context.toast("已新增Sensor")
                }
                else if (success == 1 && message == "Deleted Successfully.") {
                    dialogAdapter.notifyItemRemoved(_appSettingModel.sensorQuantity() - 1)
                    _appSettingModel.putInt("sensorQuantity", _appSettingModel.sensorQuantity() - 1)
                    _context.toast("已移除Sensor")
                }
                else {
                    _context.toast("操作失敗")
                }
            }

            override fun onException(e: Exception) {
                Log.e("Editing db", e.toString())
            }

            override fun onError(volleyError: VolleyError) {
                VolleyLog.e("ERROR", volleyError.toString())
                _context.toast("CONNECT ERROR")
            }
        })
        addSensorAction.doDbOperate(PhpUrlDto(_context).editingSensorQuantityByQuery(query, sensorId))
    }

    private fun checkInputType(): Int {
        val regWarningCondition = Regex("[0-9]*.?[0-9]+")
        val regPin = Regex("[0-9]{1,2}")
        val sensorQuantity = _appSettingModel.sensorQuantity()

        for (i in 0 until sensorQuantity) {
            val warnCondition = _appSettingModel.warningCondition(i)
            val pin = _appSettingModel.sensorPin(i)

            if (!warnCondition.matches(regWarningCondition) || !pin.matches(regPin)) {
                return i
            }
        }

        return -1
    }
}