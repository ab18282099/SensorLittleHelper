package com.example.user.soil_supervise_kotlin.Fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
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
import com.example.user.soil_supervise_kotlin.Activities.MainActivity
import com.example.user.soil_supervise_kotlin.Database.DbAction
import com.example.user.soil_supervise_kotlin.Database.IDbResponse
import com.example.user.soil_supervise_kotlin.DbDataDownload.HttpHelper
import com.example.user.soil_supervise_kotlin.DbDataDownload.IHttpAction
import com.example.user.soil_supervise_kotlin.Utility.DataWriter
import com.example.user.soil_supervise_kotlin.Utility.HttpRequest
import com.example.user.soil_supervise_kotlin.Utility.MySharedPreferences
import com.example.user.soil_supervise_kotlin.R
import com.example.user.soil_supervise_kotlin.Ui.RecyclerView.HistoryDataRecyclerAdapter
import com.example.user.soil_supervise_kotlin.Ui.RecyclerView.SimpleDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_history.*
import org.jetbrains.anko.*
import org.json.JSONObject

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
    private var _viewCount: Int = 0
    private var _sensorDataLength: Int = 0
    private var _sensorDataList = ArrayList<Array<String?>>()
    private var _sensorQuantity: Int = 5
    private var _recyclerHistory: RecyclerView? = null
    private var _sharePref: MySharedPreferences? = null
    private var _currentId1 = ""
    private var _currentId2 = ""
    private var _isSuccessLoad = false
    private var _historyDataBackUpHelper : HttpHelper? = null

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

        val layoutManger = LinearLayoutManager(context)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerHistory?.layoutManager = layoutManger
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

    fun RenewSensorTitle()
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

    fun RenewRecyclerView()
    {
        when (_sensorDataLength)
        {
            in 1..100 -> _viewCount = _sensorDataLength
            0 -> _viewCount = 0
            else -> _viewCount = 100
        }

        _mRecyclerViewAdapter = HistoryDataRecyclerAdapter(context, _sensorQuantity, _sensorDataList, _viewCount)
        _recyclerHistory?.adapter = _mRecyclerViewAdapter
        CheckButton()
    }

    fun SetCurrentId(id : String, id2 : String)
    {
        _currentId1 = id
        _currentId2 = id2
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

    fun GetCurrentId1() : String
    {
        return _currentId1
    }

    fun GetCurrentId2() : String
    {
        return _currentId2
    }

    fun GetLoadSuccess() : Boolean
    {
        return _isSuccessLoad
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
        MainActivity().LoadHistoryData(this, context, (_currentId1.toInt() - 100).toString(), (_currentId2.toInt() - 100).toString())
        CheckButton()
    }

    private fun RightView()
    {
        MainActivity().LoadHistoryData(this, context, (_currentId1.toInt() + 100).toString(), (_currentId2.toInt() + 100).toString())
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

        val loginAction = DbAction(context)
        loginAction.SetResponse(object : IDbResponse
        {
            override fun OnSuccess(jsonObject: JSONObject)
            {
                when (jsonObject.getString("message"))
                {
                    "Deleted Successfully." ->
                    {
                        MainActivity().LoadHistoryData(this@HistoryDataFragment, context, "1", "100")
                        toast("刪除成功")
                    }
                    "DB is clean." ->
                    {
                        MainActivity().LoadHistoryData(this@HistoryDataFragment, context, "1", "100")
                        toast("清空完成")
                    }
                    else ->
                    {
                        toast("操作失敗")
                    }
                }
            }

            override fun OnException(e: Exception)
            {
                Log.e("editSensor", e.toString())
                toast(e.toString())
            }

            override fun OnError(volleyError: VolleyError)
            {
                VolleyLog.e("ERROR", volleyError.toString())
                toast("CONNECT ERROR")
            }
        })
        loginAction.DoDbOperate(phpAddress)
    }

    private fun BackUpHistoryData()
    {
        val username = _sharePref!!.GetUsername()
        val password = _sharePref!!.GetPassword()
        val serverIp = _sharePref!!.GetServerIP()
        _historyDataBackUpHelper = HttpHelper.InitInstance(context)
        _historyDataBackUpHelper!!.SetHttpAction(object : IHttpAction
        {
            override fun OnHttpRequest()
            {
                val phpAddress = "http://$serverIp/android_mysql.php?&server=$serverIp&user=$username&pass=$password"
                DataWriter.WriteData(activity, _sharePref!!.GetFileSavedName()
                        , HttpRequest.DownloadFromMySQL("society", phpAddress))
                runOnUiThread { toast("備份完成") }
            }

            override fun OnException(e : Exception)
            {
                Log.e("backing up", e.toString())
                runOnUiThread { toast(e.toString()) }
            }

            override fun OnPostExecute()
            {
            }
        })
        _historyDataBackUpHelper!!.StartHttpThread()
    }
}