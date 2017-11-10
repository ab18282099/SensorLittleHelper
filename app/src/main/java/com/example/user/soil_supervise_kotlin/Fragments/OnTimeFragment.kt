package com.example.user.soil_supervise_kotlin.Fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.user.soil_supervise_kotlin.Interfaces.FragmentBackPressedListener
import com.example.user.soil_supervise_kotlin.OtherClass.HttpRequest
import com.example.user.soil_supervise_kotlin.OtherClass.ProgressDialog
import com.example.user.soil_supervise_kotlin.OtherClass.MySharedPreferences
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
        fun newInstance(): OnTimeFragment
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
    private var _httpThread: HandlerThread? = null
    private var _threadHandler: Handler? = null
    private var _progressDialog: AlertDialog? = null

    private var _recyclerOnTime: RecyclerView? = null
    private var _onTimeRecyclerAdapter: OnTimeRecyclerAdapter? = null
    private var _initRecyclerAdapter: InitRecyclerAdapter? = null

    private var _sharePref: MySharedPreferences? = null

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

        _sharePref = MySharedPreferences.initInstance(activity)

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

    override fun OnFragmentBackPressed()
    {
        _recycleThread()

        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager
        vpMain.currentItem = 1
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        Log.e("OnTimeFragment", "onDestroyView")

        _recycleThread()
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

    inner class OnTimeRecyclerAdapter : RecyclerView.Adapter<OnTimeFragment.OnTimeRecyclerAdapter.ViewHolder>()
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
                    val sensorVisibility = _sharePref!!.getSensorVisibility(position - 1)

                    if (sensorVisibility == View.VISIBLE)
                    {
                        holder!!.tx_on_time_title!!.visibility = sensorVisibility
                        holder.tx_on_time_text!!.visibility = sensorVisibility

                        holder.tx_on_time_title!!.text = _sharePref!!.getSensorName(position - 1)

                        val warnCondition = _sharePref!!.getSensorCondition(position - 1).toFloat()
                        val sensorDataFloat: Float

                        if (_sensorDataList[position] == "") sensorDataFloat = (-1).toFloat()
                        else sensorDataFloat = _sensorDataList[position]!!.toFloat()

                        if (_isWarning(sensorDataFloat, warnCondition))
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

    inner class InitRecyclerAdapter : RecyclerView.Adapter<OnTimeFragment.InitRecyclerAdapter.ViewHolder>()
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

    inner class SimpleDividerItemDecoration constructor(context: Context) : RecyclerView.ItemDecoration()
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

    fun setSensorQuantity(quantity: Int)
    {
        _sensorQuantity = quantity
    }

    fun tryLoadLastData(user: String, pass: String)
    {
        Log.e("OnTimeFragment", "tryLoadLastData")

        _sensorDataList.clear()

        val queue: RequestQueue = Volley.newRequestQueue(context)
        val progressDialog = ProgressDialog.dialogProgress(activity, "連接中…", View.VISIBLE)

        if (!progressDialog.isShowing)
        {
            progressDialog.show()
            progressDialog.setCancelable(false)
        }
        val ServerIP = _sharePref!!.getServerIP()
        val phpAddress = "http://$ServerIP/android_mysql_last.php?&server=$ServerIP&user=$user&pass=$pass"
        val connectRequest = JsonObjectRequest(phpAddress, null, { jsonObject ->
            try
            {
                val sensorQuantity = _sharePref!!.getSensorQuantity()
                val sensorData = arrayOfNulls<String>(sensorQuantity + 2)

                for (i in 0 until sensorQuantity + 2)
                {
                    if (i == 0) sensorData[i] = jsonObject?.getString("ID")
                    else if (i == sensorQuantity + 1) sensorData[i] = jsonObject?.getString("time")
                    else
                    {
                        sensorData[i] = jsonObject?.getString("sensor_" + (i).toString())
                    }
                }
                _sensorDataList.addAll(sensorData)

                val layoutManger = LinearLayoutManager(activity)
                layoutManger.orientation = LinearLayoutManager.VERTICAL
                _recyclerOnTime?.layoutManager = layoutManger

                _onTimeRecyclerAdapter = OnTimeRecyclerAdapter()
                _recyclerOnTime?.adapter = _onTimeRecyclerAdapter
                _recyclerOnTime?.addItemDecoration(SimpleDividerItemDecoration(context))

                if (progressDialog.isShowing)
                {
                    progressDialog.dismiss()
                }

                toast("連接成功")
                _warningFunction(_sensorDataList)
            }
            catch (e: Exception)
            {
                Log.e("LoadOnTimeData", e.toString())
                if (progressDialog.isShowing)
                {
                    progressDialog.dismiss()
                }
                toast(e.toString())
            }
        }, { volleyError ->
            if (progressDialog.isShowing)
            {
                progressDialog.dismiss()
            }
            VolleyLog.e("ERROR", volleyError.toString())
            toast("CONNECT ERROR")
        })
        val Timeout = 9000
        val policy = DefaultRetryPolicy(Timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        connectRequest.retryPolicy = policy
        queue.add(connectRequest)
    }

    private fun _warningFunction(sensorDataList: ArrayList<String?>)
    {
        val toggleSensorList = ArrayList<String>()
        val toggleSensorPinList = ArrayList<String>()
        val toggleText: String
        val togglePin: String

        for (i in 1.._sensorQuantity)
        {
            val warnCondition = _sharePref!!.getSensorCondition(i - 1).toFloat()
            val sensorDataFloat = if (sensorDataList[i] == "") (-1).toFloat() else sensorDataList[i]!!.toFloat()

            if (_sharePref!!.getSensorVisibility(i - 1) == View.VISIBLE)
            {
                if (_isWarning(sensorDataFloat, warnCondition))
                {
                    if (_sharePref!!.getPinState(i - 1) == "OFF")
                    {
                        toggleSensorList.add(_sharePref!!.getSensorName(i - 1))
                        toggleSensorPinList.add(_sharePref!!.getSensorPin(i - 1))
                    }
                }
                else
                {
                    if (_sharePref!!.getPinState(i - 1) == "ON")
                    {
                        toggleSensorList.add(_sharePref!!.getSensorName(i - 1))
                        toggleSensorPinList.add(_sharePref!!.getSensorPin(i - 1))
                    }
                }
            }
        }

        if (toggleSensorList.isNotEmpty() && toggleSensorPinList.isNotEmpty())
        {
            toggleText = toggleSensorList.joinToString(separator = ", ")
            togglePin = toggleSensorPinList.joinToString(separator = ",")

            when(_sharePref!!.getIsAutoToggle())
            {
                true ->
                {
                    val autoDialog = _autoToggle(togglePin, "發現狀態改變！")
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

    private fun _isWarning(sensorData: Float?, warnConditional: Float?): Boolean
    {
        val invalidFloat = (-1).toFloat()

        if (sensorData != null && warnConditional != null && sensorData != invalidFloat && sensorData < warnConditional)
        {
            return true
        }

        return false
    }

    private fun _autoToggle(togglePin: String, message: String): AlertDialog
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

                val ipAddress = _sharePref!!.getIPAddress()
                val port = _sharePref!!.getPort()
                _httpThread(ipAddress, port, togglePin)
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
            _recycleThread()
        }

        return dialog
    }

    private fun _httpThread(ipAddress: String, port: String, parameterValue: String)
    {
        _progressDialog = ProgressDialog.dialogProgress(activity, "連接中…", View.VISIBLE)
        _progressDialog!!.show()
        _progressDialog!!.setCancelable(false)

        _httpThread = HandlerThread("togglePin")
        _httpThread!!.start()
        _threadHandler = Handler(_httpThread!!.looper)
        _threadHandler!!.post {
            if (ipAddress.isNotEmpty() && port.isNotEmpty())
            {
                var requestReply: String? = ""
                try
                {
                    requestReply = HttpRequest.sendRequest(parameterValue, ipAddress, port, "pin")
                }
                catch (e: Exception)
                {
                    Log.e("toggling", e.toString())
                }

                if (requestReply != null && requestReply.isNotEmpty())
                {
                    _postExecute(requestReply)
                }
            }
        }
    }

    private fun _postExecute(requestReply: String)
    {
        if (_progressDialog!!.isShowing) _progressDialog!!.dismiss()

        if (requestReply == "THIS PIN NOT IN SERVICE")
        {
            val resultDialog = ProgressDialog.dialogProgress(activity, requestReply, View.GONE)
            resultDialog.show()
        }
        else
        {
            try
            {
                val jsonResult = JSONObject(requestReply)
                for (i in 0 until _sensorQuantity)
                {
                    _sharePref!!.PutString("getPin" + i.toString() + "State", jsonResult.getString("PIN" + _sharePref!!.getSensorPin(i)))
                }

                val resultDialog = ProgressDialog.dialogProgress(activity, "完成", View.GONE)
                resultDialog.show()
            }
            catch (e: Exception)
            {
                Log.e("RESULT NOT JSON", e.toString())

                val resultDialog = ProgressDialog.dialogProgress(activity, requestReply, View.GONE)
                resultDialog.show()
            }
        }
    }

    private fun _recycleThread()
    {
        if (_threadHandler != null && _httpThread != null)
        {
            _threadHandler?.removeCallbacksAndMessages(null)
            _httpThread?.quitSafely()
            _httpThread?.interrupt()
        }
    }
}