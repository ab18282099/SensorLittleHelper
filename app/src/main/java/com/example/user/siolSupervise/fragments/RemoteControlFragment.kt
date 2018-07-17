package com.example.user.siolSupervise.fragments

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.siolSupervise.db.HttpHelper
import com.example.user.siolSupervise.db.IHttpAction
import com.example.user.siolSupervise.ui.recyclerView.RecyclerViewOnItemClickListener
import com.example.user.siolSupervise.utility.HttpRequest
import com.example.user.siolSupervise.ui.dialog.ProgressDialog
import com.example.user.siolSupervise.models.AppSettingModel
import com.example.user.siolSupervise.R
import com.example.user.siolSupervise.ui.recyclerView.SimpleDividerItemDecoration
import com.example.user.siolSupervise.ui.recyclerView.ToggleAdapter
import kotlinx.android.synthetic.main.fragment_toggle.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.json.JSONObject

class RemoteControlFragment : BaseFragment(), FragmentBackPressedListener {
    companion object {
        fun newInstance(): RemoteControlFragment {
            val fragment = RemoteControlFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _appSettingModel: AppSettingModel? = null
    private var _recyclerToggle: RecyclerView? = null
    private var _toggleAdapter: ToggleAdapter? = null
    private var _wifiToggleHelper: HttpHelper? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _appSettingModel = AppSettingModel(activity)

        val view = inflater!!.inflate(R.layout.fragment_toggle, container, false)
        _recyclerToggle = view.findViewById<RecyclerView>(R.id._recyclerToggle) as RecyclerView

        val layoutManger = LinearLayoutManager(activity)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerToggle?.layoutManager = layoutManger

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tx_ip.text = getString(R.string.wifi_ip, _appSettingModel!!.wifiIp())
        tx_port.text = getString(R.string.wifi_port, _appSettingModel!!.wifiPort())
        tx_name.text = getString(R.string.pin_title)
        tx_pin_num.text = getString(R.string.pin_num)
        tx_pin_state.text = getString(R.string.pin_state)
        tx_pin_app.text = getString(R.string.pin_app)

        btn_pin_state.text = getString(R.string.getPinState)
        btn_pin_state.setOnClickListener {
            val ipAddress = _appSettingModel!!.wifiIp()
            val port = _appSettingModel!!.wifiPort()
            val parameterValue = "78%78"

            tryTogglePin(ipAddress, port, parameterValue)
        }
    }

    override fun onFragmentBackPressed() {
        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager
        vpMain.currentItem = 1
    }

    fun setToggleRecycler() {
        _toggleAdapter = ToggleAdapter(activity)
        _toggleAdapter?.setOnItemClickListener(object : RecyclerViewOnItemClickListener {
            override fun onRecyclerViewItemClick(view: View?, position: Int) {
                if (_appSettingModel!!.sensorVisibility(position) == View.GONE) {
                    toast("THIS SENSOR NOT IN SERVICE")
                }
                else {
                    tryTogglePin(_appSettingModel!!.wifiIp(),
                            _appSettingModel!!.wifiPort(), _appSettingModel!!.sensorPin(position))
                }
            }
        })

        _recyclerToggle?.adapter = _toggleAdapter
        _recyclerToggle?.addItemDecoration(SimpleDividerItemDecoration(activity))
    }

    private fun tryTogglePin(ipAddress: String, port: String, parameterValue: String) {
        _wifiToggleHelper = HttpHelper.useInstance()
        _wifiToggleHelper!!.setHttpAction(object : IHttpAction {
            override fun onHttpRequest() {
                val requestReply = HttpRequest.sendToggleRequest(parameterValue, ipAddress, port, "pin")
                val resultDialog = ProgressDialog.dialogProgress(activity, requestReply, View.GONE)
                resultDialog.show()

                val jsonResult = JSONObject(requestReply)

                for (i in 0 until _appSettingModel!!.sensorQuantity()) {
                    _appSettingModel!!.putString("getPin" + i.toString() + "State", jsonResult.getString("PIN" + _appSettingModel!!.sensorPin(i)))
                }

                runOnUiThread { _toggleAdapter!!.notifyDataSetChanged() }
            }

            override fun onException(e: Exception) {
                Log.e("Toggling pin", e.toString())
            }

            override fun onPostExecute() {
            }
        })
        _wifiToggleHelper!!.startHttpThread()
    }
}