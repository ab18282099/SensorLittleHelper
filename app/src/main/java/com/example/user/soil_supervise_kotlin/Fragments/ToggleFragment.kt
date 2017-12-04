package com.example.user.soil_supervise_kotlin.Fragments

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.soil_supervise_kotlin.MySqlDb.HttpHelper
import com.example.user.soil_supervise_kotlin.MySqlDb.IHttpAction
import com.example.user.soil_supervise_kotlin.Ui.RecyclerView.RecyclerViewOnItemClickListener
import com.example.user.soil_supervise_kotlin.Utility.HttpRequest
import com.example.user.soil_supervise_kotlin.Ui.ProgressDialog
import com.example.user.soil_supervise_kotlin.Model.AppSettingModel
import com.example.user.soil_supervise_kotlin.R
import com.example.user.soil_supervise_kotlin.Ui.RecyclerView.SimpleDividerItemDecoration
import com.example.user.soil_supervise_kotlin.Ui.RecyclerView.ToggleAdapter
import kotlinx.android.synthetic.main.fragment_toggle.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.json.JSONObject

class ToggleFragment : BaseFragment(), FragmentBackPressedListener
{
    companion object
    {
        fun NewInstance(): ToggleFragment
        {
            val fragment = ToggleFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _appSettingModel: AppSettingModel? = null
    private var _recyclerToggle: RecyclerView? = null
    private var _toggleAdapter: ToggleAdapter? = null
    private var _wifiToggleHelper : HttpHelper? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _appSettingModel = AppSettingModel(activity)

        val view = inflater!!.inflate(R.layout.fragment_toggle, container, false)
        _recyclerToggle = view.findViewById<RecyclerView>(R.id._recyclerToggle) as RecyclerView

        val layoutManger = LinearLayoutManager(activity)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerToggle?.layoutManager = layoutManger

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        tx_ip.text = getString(R.string.wifi_ip, _appSettingModel!!.WifiIp())
        tx_port.text = getString(R.string.wifi_port, _appSettingModel!!.WifiPort())
        tx_name.text = getString(R.string.pin_title)
        tx_pin_num.text = getString(R.string.pin_num)
        tx_pin_state.text = getString(R.string.pin_state)
        tx_pin_app.text = getString(R.string.pin_app)

        btn_pin_state.text = getString(R.string.getPinState)
        btn_pin_state.setOnClickListener {
            val ipAddress = _appSettingModel!!.WifiIp()
            val port = _appSettingModel!!.WifiPort()
            val parameterValue = "78%78"

            TryTogglePin(ipAddress, port, parameterValue)
        }
    }

    override fun OnFragmentBackPressed()
    {
        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager
        vpMain.currentItem = 1
    }

    fun SetToggleRecycler()
    {
        _toggleAdapter = ToggleAdapter(activity)
        _toggleAdapter?.SetOnItemClickListener(object : RecyclerViewOnItemClickListener
        {
            override fun OnRecyclerViewItemClick(view: View?, position: Int)
            {
                if (_appSettingModel!!.SensorVisibility(position) == View.GONE)
                {
                    toast("THIS SENSOR NOT IN SERVICE")
                }
                else
                {
                    TryTogglePin(_appSettingModel!!.WifiIp(),
                            _appSettingModel!!.WifiPort(), _appSettingModel!!.SensorPin(position))
                }
            }
        })

        _recyclerToggle?.adapter = _toggleAdapter
        _recyclerToggle?.addItemDecoration(SimpleDividerItemDecoration(activity))
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

                for (i in 0 until _appSettingModel!!.SensorQuantity())
                {
                    _appSettingModel!!.PutString("getPin" + i.toString() + "State", jsonResult.getString("PIN" + _appSettingModel!!.SensorPin(i)))
                }

                runOnUiThread { _toggleAdapter!!.notifyDataSetChanged() }
            }

            override fun OnException(e: Exception)
            {
                Log.e("Toggling pin", e.toString())
            }

            override fun OnPostExecute()
            {
            }
        })
        _wifiToggleHelper!!.StartHttpThread()
    }
}