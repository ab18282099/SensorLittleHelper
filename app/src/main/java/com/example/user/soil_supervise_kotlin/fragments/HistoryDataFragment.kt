package com.example.user.soil_supervise_kotlin.fragments

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.example.user.soil_supervise_kotlin.activities.MainActivity
import com.example.user.soil_supervise_kotlin.models.SensorDataModel
import com.example.user.soil_supervise_kotlin.db.HttpHelper
import com.example.user.soil_supervise_kotlin.db.IHttpAction
import com.example.user.soil_supervise_kotlin.utility.DataWriter
import com.example.user.soil_supervise_kotlin.utility.HttpRequest
import com.example.user.soil_supervise_kotlin.models.AppSettingModel
import com.example.user.soil_supervise_kotlin.dto.PhpUrlDto
import com.example.user.soil_supervise_kotlin.R
import com.example.user.soil_supervise_kotlin.ui.recyclerView.HistoryDataAdapter
import com.example.user.soil_supervise_kotlin.ui.recyclerView.SimpleDividerItemDecoration
import com.example.user.soil_supervise_kotlin.ui.dialog.DeleteDataDialog
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

    private var _viewCount: Int = 0
    private var _sensorDataModel = SensorDataModel()
    private var _recyclerHistory: RecyclerView? = null
    private var _appSettingModel: AppSettingModel? = null
    private var _currentId1 = ""
    private var _currentId2 = ""
    private var _isSuccessLoad = false
    private var _historyDataBackUpHelper : HttpHelper? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater!!.inflate(R.layout.fragment_history, container, false)
        _appSettingModel = AppSettingModel(activity)
        _recyclerHistory = view.findViewById<RecyclerView>(R.id._recyclerHistory) as RecyclerView
        val layoutManger = LinearLayoutManager(activity)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerHistory?.layoutManager = layoutManger
        _recyclerHistory?.addItemDecoration(SimpleDividerItemDecoration(activity))

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

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
            val dialog = DeleteDataDialog(activity, this)
            dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.setCanceledOnTouchOutside(true)
            dialog.show()
        }
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

    fun SetDataModel(model: SensorDataModel)
    {
        _sensorDataModel = model
    }

    fun RenewSensorTitle()
    {
        tx_sensor1.visibility = _appSettingModel!!.SensorVisibility(0)
        tx_sensor2.visibility = _appSettingModel!!.SensorVisibility(1)
        tx_sensor3.visibility = _appSettingModel!!.SensorVisibility(2)
        tx_sensor4.visibility = _appSettingModel!!.SensorVisibility(3)
        tx_sensor5.visibility = _appSettingModel!!.SensorVisibility(4)
        tx_id.text = getString(R.string.id)
        tx_sensor1.text = _appSettingModel!!.SensorName(0)
        tx_sensor2.text = _appSettingModel!!.SensorName(1)
        tx_sensor3.text = _appSettingModel!!.SensorName(2)
        tx_sensor4.text = _appSettingModel!!.SensorName(3)
        tx_sensor5.text = _appSettingModel!!.SensorName(4)
        tx_time.text = getString(R.string.time)
        tx_title_content.removeAllViewsInLayout()

        if (_appSettingModel!!.SensorQuantity() > 5)
        {
            tx_title_content.visibility = View.VISIBLE

            for (i in 0 until _appSettingModel!!.SensorQuantity() - 5)
            {
                val txCustomerTitle = TextView(activity)
                val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (50).toFloat(), activity.resources.displayMetrics)
                txCustomerTitle.text = _appSettingModel!!.SensorName(i + 5)
                txCustomerTitle.visibility = _appSettingModel!!.SensorVisibility(i + 5)
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
        val model = _sensorDataModel

        when (model.SensorDataLength)
        {
            in 1 until 100 -> _viewCount = model.SensorDataLength
            0 -> _viewCount = 0
            else -> _viewCount = 100
        }

        _recyclerHistory?.adapter = HistoryDataAdapter(activity, model.SensorDataList, _viewCount)
        CheckButton()
    }

    fun SetCurrentId(id : String, id2 : String)
    {
        _currentId1 = id
        _currentId2 = id2
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

    private fun LeftView()
    {
        (activity as MainActivity).LoadHistoryData(this, activity, (_currentId1.toInt() - 100).toString(), (_currentId2.toInt() - 100).toString())
        CheckButton()
    }

    private fun RightView()
    {
        (activity as MainActivity).LoadHistoryData(this, activity, (_currentId1.toInt() + 100).toString(), (_currentId2.toInt() + 100).toString())
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

    private fun BackUpHistoryData()
    {
        _historyDataBackUpHelper = HttpHelper.InitInstance(activity)
        _historyDataBackUpHelper!!.SetHttpAction(object : IHttpAction
        {
            override fun OnHttpRequest()
            {
                DataWriter.WriteData(activity, _appSettingModel!!.FileSavedName()
                        , HttpRequest.DownloadFromMySQL("society", PhpUrlDto(activity).LoadingWholeData))
                toast("備份完成")
            }

            override fun OnException(e : Exception)
            {
                Log.e("Backing up history data", e.toString())
                toast(e.toString())
            }

            override fun OnPostExecute()
            {
            }
        })
        _historyDataBackUpHelper!!.StartHttpThread()
    }
}