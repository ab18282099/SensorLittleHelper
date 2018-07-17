package com.example.user.siolSupervise.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.user.siolSupervise.R
import com.example.user.siolSupervise.db.HttpHelper
import com.example.user.siolSupervise.db.IHttpAction
import com.example.user.siolSupervise.models.AppSettingModel
import com.example.user.siolSupervise.utility.HttpRequest
import org.json.JSONObject

class AutoToggleDialog constructor(context: Context, togglePin: String, message: String) : AlertDialog(context) {
    private val _context = context
    private val _appSettingModel = AppSettingModel(context)
    private val _togglePin = togglePin
    private val _message = message
    private var _count = 5
    private var _countHandler: Handler? = null
    private var _countRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_count_down)
        val tx_count = findViewById<TextView>(R.id.tx_count)
        val btn_cancel = findViewById<Button>(R.id.btn_cancel)

        _countHandler = Handler()
        _countRunnable = Runnable {
            if (_count < 0) {
                this.dismiss()
                _count = 5
                _countHandler!!.removeCallbacks(_countRunnable)

                val ipAddress = _appSettingModel.wifiIp()
                val port = _appSettingModel.wifiPort()
                tryTogglePin(ipAddress, port, _togglePin)
            }
            else {
                _count -= 1
                tx_count.text = _context.getString(R.string.countDown, _message, (_count + 1).toString(), _togglePin)
                _countHandler!!.postDelayed(_countRunnable, 1000)
            }
        }
        _countHandler!!.post(_countRunnable)

        btn_cancel.text = _context.getString(R.string.cancel)
        btn_cancel.setOnClickListener {
            this.dismiss()
            _count = 5
            _countHandler!!.removeCallbacks(_countRunnable)
        }
    }

    private fun tryTogglePin(ipAddress: String, port: String, parameterValue: String) {
        val wifiToggleHelper = HttpHelper.useInstance()
        wifiToggleHelper!!.setHttpAction(object : IHttpAction {
            override fun onHttpRequest() {
                val requestReply = HttpRequest.sendToggleRequest(parameterValue, ipAddress, port, "pin")
                val resultDialog = ProgressDialog.dialogProgress(_context, requestReply, View.GONE)
                resultDialog.show()

                val jsonResult = JSONObject(requestReply)

                for (i in 0 until _appSettingModel.sensorQuantity()) {
                    _appSettingModel.putString("getPin" + i.toString() + "State", jsonResult.getString("PIN" + _appSettingModel.sensorPin(i)))
                }
            }

            override fun onException(e: Exception) {
                Log.e("Toggling Pin in RealTimeFragment", e.toString())
            }

            override fun onPostExecute() {
            }
        })
        wifiToggleHelper.startHttpThread()
    }
}