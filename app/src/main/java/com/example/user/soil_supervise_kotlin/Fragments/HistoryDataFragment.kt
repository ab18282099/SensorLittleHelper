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
import android.util.TypedValue
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.user.soil_supervise_kotlin.Activities.MainActivity
import com.example.user.soil_supervise_kotlin.Interfaces.FragmentBackPressedListener
import com.example.user.soil_supervise_kotlin.Interfaces.FragmentMenuItemClickListener
import com.example.user.soil_supervise_kotlin.OtherClass.DataWriter
import com.example.user.soil_supervise_kotlin.OtherClass.HttpRequest
import com.example.user.soil_supervise_kotlin.OtherClass.ProgressDialog
import com.example.user.soil_supervise_kotlin.OtherClass.MySharedPreferences
import com.example.user.soil_supervise_kotlin.R
import kotlinx.android.synthetic.main.fragment_history.*
import org.jetbrains.anko.*

class HistoryDataFragment : BaseFragment(), FragmentBackPressedListener, FragmentMenuItemClickListener
{
    companion object
    {
        fun NewInstance(): HistoryDataFragment
        {
            val fragment = HistoryDataFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _mRecyclerViewAdapter: HistoryDataRecyclerAdapter? = null

    private var _viewCount: Int? = null

    private var _sensorDataLength: Int = 0
    private var _sensorDataList = ArrayList<Array<String?>>()
    private var _sensorQuantity: Int = 5

    private var _recyclerHistory: RecyclerView? = null

    private var _sharePref: MySharedPreferences? = null

    private var _currentId1 = ""
    private var _currentId2 = ""

    private var _isSuccessLoad = false

    private var _httpThread : HandlerThread? = null
    private var _threadHandler : Handler? = null

    inner class HistoryDataRecyclerAdapter : RecyclerView.Adapter<HistoryDataFragment.HistoryDataRecyclerAdapter.ViewHolder>()
    {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            // id
            var tx_item_id: TextView? = null
            // default 5 sensor
            var tx_item_sensor1: TextView? = null
            var tx_item_sensor2: TextView? = null
            var tx_item_sensor3: TextView? = null
            var tx_item_sensor4: TextView? = null
            var tx_item_sensor5: TextView? = null
            // customer sensor
            var tx_item_content: LinearLayout? = null
            // time
            var tx_item_time: TextView? = null

            init
            {
                tx_item_id = itemView.findViewById<TextView>(R.id.tx_item_id) as TextView
                tx_item_sensor1 = itemView.findViewById<TextView>(R.id.tx_item_moisture) as TextView
                tx_item_sensor2 = itemView.findViewById<TextView>(R.id.tx_item_light) as TextView
                tx_item_sensor3 = itemView.findViewById<TextView>(R.id.tx_item_sensor3) as TextView
                tx_item_sensor4 = itemView.findViewById<TextView>(R.id.tx_item_sensor4) as TextView
                tx_item_sensor5 = itemView.findViewById<TextView>(R.id.tx_item_sensor5) as TextView
                tx_item_content = itemView.findViewById<LinearLayout>(R.id.tx_item_content) as LinearLayout
                tx_item_time = itemView.findViewById<TextView>(R.id.tx_item_time) as TextView
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HistoryDataFragment.HistoryDataRecyclerAdapter.ViewHolder
        {
            Log.e("HistoryDataFragment", "onCreateViewHolder")

            val converterView = LayoutInflater.from(parent?.context)
                    .inflate(R.layout.item_sensor_data, parent, false)
            return ViewHolder(converterView)
        }

        override fun onBindViewHolder(holder: HistoryDataFragment.HistoryDataRecyclerAdapter.ViewHolder?, position: Int)
        {
            Log.e("HistoryDataFragment", "onBindViewHolder")

            holder!!.tx_item_sensor1!!.visibility = _sharePref!!.GetSensorVisibility(0)
            holder.tx_item_sensor2!!.visibility = _sharePref!!.GetSensorVisibility(1)
            holder.tx_item_sensor3!!.visibility = _sharePref!!.GetSensorVisibility(2)
            holder.tx_item_sensor4!!.visibility = _sharePref!!.GetSensorVisibility(3)
            holder.tx_item_sensor5!!.visibility = _sharePref!!.GetSensorVisibility(4)

            holder.tx_item_content?.removeAllViewsInLayout()

            if (_sensorDataList.isNotEmpty())
            {
                if (_sensorQuantity > 5)
                {
                    for (i in 0 until _sensorQuantity - 5)
                    {
                        val txCustomer = TextView(context)
                        val sensorData = _sensorDataList[i + 6][position]
                        SetSensorText(txCustomer, sensorData, i + 5)
                        txCustomer.visibility = _sharePref!!.GetSensorVisibility(i + 5)

                        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (50).toFloat(), context.resources.displayMetrics)
                        txCustomer.layoutParams = LinearLayout.LayoutParams(height.toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)

                        holder.tx_item_content!!.addView(txCustomer)
                    }
                }

                holder.tx_item_id!!.text = _sensorDataList[0][position] // id dataList[0]
                holder.tx_item_time!!.text = _sensorDataList[_sensorQuantity + 1][position] // id dataList[last]

                SetSensorText(holder.tx_item_sensor1, _sensorDataList[1][position], 0)
                SetSensorText(holder.tx_item_sensor2, _sensorDataList[2][position], 1)
                SetSensorText(holder.tx_item_sensor3, _sensorDataList[3][position], 2)
                SetSensorText(holder.tx_item_sensor4, _sensorDataList[4][position], 3)
                SetSensorText(holder.tx_item_sensor5, _sensorDataList[5][position], 4)
            }
        }

        override fun getItemCount(): Int
        {
            val viewCount = _viewCount
            if (viewCount != null && viewCount > 0) return viewCount
            return 0
        }

        private fun SetSensorText(textView: TextView?, sensorData: String?, position: Int)
        {
            if (sensorData == "")
            {
                textView!!.text = sensorData
            }
            else
            {
                val warnConditional1 = _sharePref!!.GetSensorCondition(position).toFloat()

                if (sensorData!!.toFloat() < warnConditional1)
                {
                    textView!!.text = sensorData
                    textView.textColor = Color.RED
                }
                else
                {
                    textView!!.text = sensorData
                    textView.textColor = Color.BLACK
                }
            }
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

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        Log.e("HistoryDataFragment", "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.e("HistoryDataFragment", "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater!!.inflate(R.layout.fragment_history, container, false)

        _sharePref = MySharedPreferences.InitInstance(activity)

        _recyclerHistory = view.findViewById<RecyclerView>(R.id._recyclerHistory) as RecyclerView

        val layoutManger = LinearLayoutManager(this.context)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerHistory?.layoutManager = layoutManger

        _mRecyclerViewAdapter = HistoryDataRecyclerAdapter()
        _recyclerHistory?.adapter = _mRecyclerViewAdapter
        _recyclerHistory?.addItemDecoration(SimpleDividerItemDecoration(context))

        Log.e("HistoryDataFragment", "onCreateView")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        Log.e("HistoryDataFragment", "onActivityCreated")
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        Log.e("HistoryDataFragment", "onViewCreated")

        btn_left.text = "上一頁"
        btn_right.text = "下一頁"
        btn_left.setOnClickListener {
            LeftView()
        }

        btn_right.setOnClickListener {
            RightView()
        }

        btn_deleted.text = getString(R.string.deleted)
        btn_deleted.setOnClickListener {
            val dialog = SetDeletedDialog(activity)
            dialog.show()
            dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.setCanceledOnTouchOutside(true)
        }
    }

    override fun onStart()
    {
        super.onStart()
        Log.e("HistoryDataFragment", "onStart")
    }

    override fun onResume()
    {
        super.onResume()
        Log.e("HistoryDataFragment", "onResume")
    }

    override fun onPause()
    {
        super.onPause()
        Log.e("HistoryDataFragment", "onPause")
    }

    override fun onStop()
    {
        super.onStop()
        Log.e("HistoryDataFragment", "onStop")
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        Log.e("HistoryDataFragment", "onDestroyView")
    }

    override fun onDestroy()
    {
        super.onDestroy()
        RecycleThread()
        Log.e("HistoryDataFragment", "onDestroy")
    }

    override fun onDetach()
    {
        super.onDetach()
        Log.e("HistoryDataFragment", "onDetach")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean)
    {
        super.setUserVisibleHint(isVisibleToUser)
        Log.e("HistoryDataFragment", isVisibleToUser.toString())
    }

    /*
    No need onOptionsItemSelected, just set clickListener in MainActivity by toolbar.setMenuItemClickListener
    */

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?)
    {
        inflater!!.inflate(R.menu.menu_history, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun FragmentMenuItemClickListenerObject(): (MenuItem) -> Boolean
    {
        return { item ->

            when(item.itemId)
            {
                R.id.menu_backup ->
                {
                    BackUpHistoryData()
                }
            }

            true
        }
    }

    override fun OnFragmentBackPressed()
    {
        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager
        vpMain.currentItem = 1
    }

    fun SetSensorTitle()
    {
        tx_sensor1.visibility = _sharePref!!.GetSensorVisibility(0)
        tx_sensor2.visibility = _sharePref!!.GetSensorVisibility(1)
        tx_sensor3.visibility = _sharePref!!.GetSensorVisibility(2)
        tx_sensor4.visibility = _sharePref!!.GetSensorVisibility(3)
        tx_sensor5.visibility = _sharePref!!.GetSensorVisibility(4)

        tx_id.text = getString(R.string.id)
        tx_sensor1.text = _sharePref!!.GetSensorName(0)
        tx_sensor2.text = _sharePref!!.GetSensorName(1)
        tx_sensor3.text = _sharePref!!.GetSensorName(2)
        tx_sensor4.text = _sharePref!!.GetSensorName(3)
        tx_sensor5.text = _sharePref!!.GetSensorName(4)
        tx_time.text = getString(R.string.time)

        tx_title_content.removeAllViewsInLayout()

        if (_sensorQuantity > 5)
        {
            tx_title_content.visibility = View.VISIBLE

            for (i in 0 until _sensorQuantity - 5)
            {
                val txCustomerTitle = TextView(context)
                txCustomerTitle.text = _sharePref!!.GetSensorName(i + 5)
                txCustomerTitle.visibility = _sharePref!!.GetSensorVisibility(i + 5)

                val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (50).toFloat(), context.resources.displayMetrics)
                txCustomerTitle.layoutParams = LinearLayout.LayoutParams(height.toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)

                tx_title_content.addView(txCustomerTitle)
            }
        }
        else
        {
            tx_title_content.visibility = View.GONE
        }

        sensorTitle!!.invalidate()
    }

    fun SetCurrentId(id : String, id2 : String)
    {
        _currentId1 = id
        _currentId2 = id2
    }

    fun GetCurrentId1() : String
    {
        return _currentId1
    }

    fun GetCurrentId2() : String
    {
        return _currentId2
    }

    fun SetSensorQuantity(quantity: Int)
    {
        _sensorQuantity = quantity
    }

    fun SetJsonArrayLength(length: Int)
    {
        _sensorDataLength = length
    }

    fun SetSensorDataList(dataList: ArrayList<Array<String?>>)
    {
        _sensorDataList = dataList
    }

    fun SetLoadSuccess(isSuccess : Boolean)
    {
        _isSuccessLoad = isSuccess
    }

    fun GetLoadSuccess() : Boolean
    {
        return _isSuccessLoad
    }

    fun RenewRecyclerView()
    {
        if (_sensorDataLength in 1..100)
        {
            _viewCount = _sensorDataLength
            _mRecyclerViewAdapter?.notifyDataSetChanged()
        }
        else if (_sensorDataLength == 0)
        {
            _viewCount = 0
            _mRecyclerViewAdapter?.notifyDataSetChanged()
        }
        else
        {
            _viewCount = 100
            _mRecyclerViewAdapter?.notifyDataSetChanged()
        }

        CheckButton()
    }

    fun RecycleThread()
    {
        if (_threadHandler != null && _httpThread != null)
        {
            _threadHandler?.removeCallbacksAndMessages(null)
            _httpThread?.quitSafely()
            _httpThread?.interrupt()
        }
    }

    private fun SetDeletedDialog(context: Context): AlertDialog
    {
        val nullParent: ViewGroup? = null
        val convertView = LayoutInflater.from(context).inflate(R.layout.dialog_deleted, nullParent)

        val edit_from = convertView.findViewById<EditText>(R.id.edit_from) as EditText
        val edit_to = convertView.findViewById<EditText>(R.id.edit_to) as EditText
        val btn_clean = convertView.findViewById<Button>(R.id.btn_clean) as Button
        val btn_confirm = convertView.findViewById<Button>(R.id.btn_confirm) as Button

        val dialog = android.app.AlertDialog.Builder(context).setView(convertView).create()

        btn_clean.text = getString(R.string.clean_db)
        btn_clean.setOnClickListener {
            val ServerIP = _sharePref!!.GetServerIP()
            val user = _sharePref!!.GetUsername()
            val pass = _sharePref!!.GetPassword()
            val phpAddress = "http://$ServerIP/clean_db.php?&server=$ServerIP&user=$user&pass=$pass"

            alert("你要確定喔?") {
                yesButton {
                    TryEditDataBase(phpAddress)
                }
                noButton { }
            }.show()
        }

        btn_confirm.text = getString(R.string.deleted)
        btn_confirm.setOnClickListener {
            val ServerIP = _sharePref!!.GetServerIP()
            val user = _sharePref!!.GetUsername()
            val pass = _sharePref!!.GetPassword()
            val id = edit_from.text.toString()
            val id2 = edit_to.text.toString()

            val regIdDeleted = Regex("[1-9]\\d*")

            if (id.matches(regIdDeleted) && id2.matches(regIdDeleted))
            {
                val phpAddress = "http://$ServerIP/deletedjson.php?&server=$ServerIP&user=$user&pass=$pass&id=$id&id2=$id2"
                alert("你要確定喔?") {
                    yesButton {
                        TryEditDataBase(phpAddress)
                    }
                    noButton { }
                }.show()
            }
            else
            {
                val v = context.vibrator
                v.vibrate(500)
                toast("輸入有誤")
            }
        }

        return dialog
    }

    private fun LeftView()
    {
        MainActivity().LoadHistoryData(this, activity, (_currentId1.toInt() - 100).toString(), (_currentId2.toInt() - 100).toString())
        CheckButton()
    }

    private fun RightView()
    {
        MainActivity().LoadHistoryData(this, activity, (_currentId1.toInt() + 100).toString(), (_currentId2.toInt() + 100).toString())
        CheckButton()
    }

    private fun CheckButton()
    {
        if (_viewCount == 0)
        {
            btn_left.isEnabled = false
            btn_right.isEnabled = false
            btn_deleted.isEnabled = false
        }
        else
        {
            when (_currentId1 == "1")
            {
                true->
                {
                    btn_left.isEnabled = false
                }
                false->
                {
                    btn_left.isEnabled = true
                }
            }

            btn_right.isEnabled = true
            btn_deleted.isEnabled = true
        }
    }

    private fun TryEditDataBase(phpAddress: String)
    {
        Log.e("HistoryFragment", "TryEditDataBase")

        val queue: RequestQueue = Volley.newRequestQueue(context)
        val progressDialog = ProgressDialog.DialogProgress(activity, "連接中…", View.VISIBLE)

        if (!progressDialog.isShowing)
        {
            progressDialog.show()
            progressDialog.setCancelable(false)
        }

        val connectRequest = JsonObjectRequest(phpAddress, null, { jsonObject ->
            try
            {
                val success = jsonObject?.getString("message")
                if (success == "Deleted Successfully.")
                {
                    if (progressDialog.isShowing)
                    {
                        progressDialog.dismiss()
                    }

                    MainActivity().LoadHistoryData(this@HistoryDataFragment, activity, "1", "100")
                    toast("操作成功")
                }
                else if (success == "DB is clean.")
                {
                    if (progressDialog.isShowing)
                    {
                        progressDialog.dismiss()
                    }

                    MainActivity().LoadHistoryData(this@HistoryDataFragment, activity, "1", "100")
                    toast("操作成功")
                }
                else
                {
                    if (progressDialog.isShowing)
                    {
                        progressDialog.dismiss()
                    }

                    //MainActivity().LoadHistoryData(this@HistoryDataFragment, activity)
                    toast("操作失敗")
                }
            }
            catch (e: Exception)
            {
                Log.e("editSensor", e.toString())
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

    private fun BackUpHistoryData()
    {
        val username = _sharePref!!.GetUsername()
        val password = _sharePref!!.GetPassword()
        val serverIp = _sharePref!!.GetServerIP()
        val progressDialog = ProgressDialog.DialogProgress(activity, "下載中", View.VISIBLE)
        progressDialog.setCancelable(false)
        progressDialog.show()

        _httpThread = HandlerThread("history_data_backup")
        _httpThread!!.start()
        _threadHandler = Handler(_httpThread!!.looper)
        _threadHandler!!.post {
            try
            {
                val phpAddress = "http://$serverIp/android_mysql.php?&server=$serverIp&user=$username&pass=$password"
                val data = HttpRequest.DownloadFromMySQL("society", phpAddress)
                DataWriter.WriteData(activity, _sharePref!!.GetFileSavedName(), data)
                runOnUiThread { toast("備份完成") }
                progressDialog.dismiss()
            }
            catch (e : Exception)
            {
                runOnUiThread { toast(e.toString()) }
                progressDialog.dismiss()
            }
        }
    }
}