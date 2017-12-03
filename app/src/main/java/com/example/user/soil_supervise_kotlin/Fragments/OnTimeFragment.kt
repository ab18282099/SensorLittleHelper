package com.example.user.soil_supervise_kotlin.Fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
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
import com.example.user.soil_supervise_kotlin.Database.DbAction
import com.example.user.soil_supervise_kotlin.Database.IDbResponse
import com.example.user.soil_supervise_kotlin.DbDataDownload.HttpHelper
import com.example.user.soil_supervise_kotlin.DbDataDownload.IHttpAction
import com.example.user.soil_supervise_kotlin.Utility.HttpRequest
import com.example.user.soil_supervise_kotlin.Ui.ProgressDialog
import com.example.user.soil_supervise_kotlin.Utility.MySharedPreferences
import com.example.user.soil_supervise_kotlin.R
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
    private var _onTimeRecyclerAdapter: OnTimeRecyclerAdapter? = null
    private var _initRecyclerAdapter: InitRecyclerAdapter? = null

    private var _sharePref: MySharedPreferences? = null

    private var _wifiToggleHelper : HttpHelper? = null

    private inner class OnTimeRecyclerAdapter : RecyclerView.Adapter<OnTimeFragment.OnTimeRecyclerAdapter.ViewHolder>()
    {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            var tx_on_time_title: TextView? = null
            var tx_on_time_text: TextView? = null

            init
            {
                tx_on_time_title = itemView.findViewById<TextView>(R.id.tx_on_time_title) as TextView
                tx_on_time_text = itemView.findViewById<TextView>(R.id.tx_on_time_text) as TextView
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder
        {
            val converterView = LayoutInflater.from(parent?.context)
                    .inflate(R.layout.item_on_time, parent, false)
            return ViewHolder(converterView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int)
        {
            if (_sensorDataList.isNotEmpty())
            {
                if (position == 0)
                {
                    holder!!.tx_on_time_title!!.text = getString(R.string.ID)
                    holder.tx_on_time_text!!.text = _sensorDataList[position]

                    holder.tx_on_time_text!!.visibility = View.VISIBLE
                    holder.tx_on_time_title!!.visibility = View.VISIBLE

                    holder.tx_on_time_text!!.setTextColor(Color.BLACK)
                    holder.tx_on_time_title!!.setTextColor(Color.BLACK)
                }
                else if (position in 1.._sensorQuantity)
                {
                    val sensorVisibility = _sharePref!!.GetSensorVisibility(position - 1)

                    if (sensorVisibility == View.VISIBLE)
                    {
                        holder!!.tx_on_time_title!!.visibility = sensorVisibility
                        holder.tx_on_time_text!!.visibility = sensorVisibility

                        holder.tx_on_time_title!!.text = _sharePref!!.GetSensorName(position - 1)

                        val warnCondition = _sharePref!!.GetSensorCondition(position - 1).toFloat()
                        val sensorDataFloat: Float

                        if (_sensorDataList[position] == "") sensorDataFloat = (-1).toFloat()
                        else sensorDataFloat = _sensorDataList[position]!!.toFloat()

                        if (IsWarning(sensorDataFloat, warnCondition))
                        {
                            holder.tx_on_time_text!!.text = getString(R.string.warn, _sensorDataList[position], String.format("%.2f", warnCondition))
                            holder.tx_on_time_text!!.setTextColor(Color.RED)
                            holder.tx_on_time_title!!.setTextColor(Color.RED)
                        }
                        else
                        {
                            holder.tx_on_time_text!!.text = _sensorDataList[position]

                            holder.tx_on_time_text!!.setTextColor(Color.BLACK)
                            holder.tx_on_time_title!!.setTextColor(Color.BLACK)
                        }
                    }
                    else
                    {
                        holder!!.tx_on_time_title!!.visibility = sensorVisibility
                        holder.tx_on_time_text!!.visibility = sensorVisibility
                    }
                }
                else
                {
                    holder!!.tx_on_time_title!!.text = getString(R.string.TIME)
                    holder.tx_on_time_text!!.text = _sensorDataList[position]

                    holder.tx_on_time_text!!.visibility = View.VISIBLE
                    holder.tx_on_time_title!!.visibility = View.VISIBLE

                    holder.tx_on_time_text!!.setTextColor(Color.BLACK)
                    holder.tx_on_time_title!!.setTextColor(Color.BLACK)
                }
            }
            else
            {
                holder!!.tx_on_time_title!!.visibility = View.GONE
                holder.tx_on_time_text!!.visibility = View.GONE
            }
        }

        override fun getItemCount(): Int
        {
            if (_sensorDataList.isEmpty()) return 0
            else return _sensorQuantity + 2
        }
    }

    private inner class InitRecyclerAdapter : RecyclerView.Adapter<OnTimeFragment.InitRecyclerAdapter.ViewHolder>()
    {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            private var txOnTimeTitle: TextView? = null
            private var txOnTimeText: TextView? = null

            init
            {
                txOnTimeTitle = itemView.findViewById<TextView>(R.id.tx_on_time_title) as TextView
                txOnTimeText = itemView.findViewById<TextView>(R.id.tx_on_time_text) as TextView
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder
        {
            val converterView = LayoutInflater.from(parent?.context)
                    .inflate(R.layout.item_on_time, parent, false)
            return ViewHolder(converterView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int)
        {

        }

        override fun getItemCount(): Int
        {
            return 0
        }
    }

    private inner class SimpleDividerItemDecoration constructor(context: Context) : RecyclerView.ItemDecoration()
    {
        private val _mDivider = ContextCompat.getDrawable(context, R.drawable.divider_line)

        override fun onDrawOver(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?)
        {
            val left = parent!!.paddingLeft
            val right = parent.width - parent.paddingRight

            val childCount = parent.childCount
            for (i in 0 until childCount)
            {
                val child = parent.getChildAt(i)

                val params = child.layoutParams as RecyclerView.LayoutParams

                val top = child.bottom + params.bottomMargin
                val bottom = top + _mDivider!!.intrinsicHeight

                _mDivider.setBounds(left, top, right, bottom)
                _mDivider.draw(c)
            }
        }
    }

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        Log.e("OnTimeFragment", "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.e("OnTimeFragment", "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater!!.inflate(R.layout.fragment_on_time, container, false)

        _sharePref = MySharedPreferences.InitInstance(activity)

        _recyclerOnTime = view.findViewById<RecyclerView>(R.id.recycler_on_time) as RecyclerView

        val layoutManger = LinearLayoutManager(this.context)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerOnTime?.layoutManager = layoutManger

        _initRecyclerAdapter = InitRecyclerAdapter()
        _recyclerOnTime?.adapter = _initRecyclerAdapter

        Log.e("OnTimeFragment", "onCreateView")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        Log.e("OnTimeFragment", "onActivityCreated")
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        Log.e("OnTimeFragment", "onViewCreated")

    }

    override fun onStart()
    {
        super.onStart()
        Log.e("OnTimeFragment", "onStart")
    }

    override fun onResume()
    {
        super.onResume()
        Log.e("OnTimeFragment", "onResume")
    }

    override fun onPause()
    {
        super.onPause()
        Log.e("OnTimeFragment", "onPause")
    }

    override fun onStop()
    {
        super.onStop()
        Log.e("OnTimeFragment", "onStop")
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        Log.e("OnTimeFragment", "onDestroyView")
    }

    override fun onDestroy()
    {
        super.onDestroy()
        Log.e("OnTimeFragment", "onDestroy")
    }

    override fun onDetach()
    {
        super.onDetach()
        Log.e("OnTimeFragment", "onDetach")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean)
    {
        super.setUserVisibleHint(isVisibleToUser)
        Log.e("OnTimeFragment", isVisibleToUser.toString())
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
        Log.e("OnTimeFragment", "TryLoadLastData")

        _sensorDataList.clear()

        val ServerIP = _sharePref!!.GetServerIP()
        val phpAddress = "http://$ServerIP/android_mysql_last.php?&server=$ServerIP&user=$user&pass=$pass"
        val refreshDataAction = DbAction(context)
        refreshDataAction.SetResponse(object : IDbResponse
        {
            override fun OnSuccess(jsonObject: JSONObject)
            {
                val sensorQuantity = _sharePref!!.GetSensorQuantity()
                val sensorData = arrayOfNulls<String>(sensorQuantity + 2)

                for (i in 0 until sensorQuantity + 2)
                {
                    if (i == 0)
                        sensorData[i] = jsonObject.getString("ID")
                    else if (i == sensorQuantity + 1)
                        sensorData[i] = jsonObject.getString("time")
                    else
                        sensorData[i] = jsonObject.getString("sensor_" + (i).toString())
                }

                _sensorDataList.addAll(sensorData)
                val layoutManger = LinearLayoutManager(activity)
                layoutManger.orientation = LinearLayoutManager.VERTICAL
                _recyclerOnTime?.layoutManager = layoutManger
                _onTimeRecyclerAdapter = OnTimeRecyclerAdapter()
                _recyclerOnTime?.adapter = _onTimeRecyclerAdapter
                _recyclerOnTime?.addItemDecoration(SimpleDividerItemDecoration(context))
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
            val warnCondition = _sharePref!!.GetSensorCondition(i - 1).toFloat()
            val sensorDataFloat = if (sensorDataList[i] == "") (-1).toFloat() else sensorDataList[i]!!.toFloat()

            if (_sharePref!!.GetSensorVisibility(i - 1) == View.VISIBLE)
            {
                if (IsWarning(sensorDataFloat, warnCondition))
                {
                    if (_sharePref!!.GetPinState(i - 1) == "OFF")
                    {
                        toggleSensorList.add(_sharePref!!.GetSensorName(i - 1))
                        toggleSensorPinList.add(_sharePref!!.GetSensorPin(i - 1))
                    }
                }
                else
                {
                    if (_sharePref!!.GetPinState(i - 1) == "ON")
                    {
                        toggleSensorList.add(_sharePref!!.GetSensorName(i - 1))
                        toggleSensorPinList.add(_sharePref!!.GetSensorPin(i - 1))
                    }
                }
            }
        }

        if (toggleSensorList.isNotEmpty() && toggleSensorPinList.isNotEmpty())
        {
            toggleText = toggleSensorList.joinToString(separator = ", ")
            togglePin = toggleSensorPinList.joinToString(separator = ",")

            when(_sharePref!!.GetIsAutoToggle())
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

                val ipAddress = _sharePref!!.GetIPAddress()
                val port = _sharePref!!.GetPort()
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
        _wifiToggleHelper = HttpHelper.InitInstance(context)
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
                    _sharePref!!.PutString("getPin" + i.toString() + "State", jsonResult.getString("PIN" + _sharePref!!.GetSensorPin(i)))
                }
            }

            override fun OnException(e: Exception)
            {
                Log.e("toggling", e.toString())
            }

            override fun OnPostExecute()
            {
            }
        })
    }
}