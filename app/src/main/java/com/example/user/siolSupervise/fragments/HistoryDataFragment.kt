package com.example.user.siolSupervise.fragments

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.example.user.siolSupervise.activities.MainActivity
import com.example.user.siolSupervise.models.SensorDataModel
import com.example.user.siolSupervise.db.HttpHelper
import com.example.user.siolSupervise.db.IHttpAction
import com.example.user.siolSupervise.utility.DataWriter
import com.example.user.siolSupervise.utility.HttpRequest
import com.example.user.siolSupervise.models.AppSettingModel
import com.example.user.siolSupervise.dto.PhpUrlDto
import com.example.user.siolSupervise.R
import com.example.user.siolSupervise.ui.recyclerView.HistoryDataAdapter
import com.example.user.siolSupervise.ui.recyclerView.SimpleDividerItemDecoration
import com.example.user.siolSupervise.ui.dialog.DeleteDataDialog
import kotlinx.android.synthetic.main.fragment_history.*
import org.jetbrains.anko.*

class HistoryDataFragment : BaseFragment(), FragmentBackPressedListener, FragmentMenuItemClickListener {
    companion object {
        fun newInstance(): HistoryDataFragment {
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
    private var _historyDataBackUpHelper: HttpHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_history, container, false)
        _appSettingModel = AppSettingModel(activity)
        _recyclerHistory = view.findViewById<RecyclerView>(R.id._recyclerHistory) as RecyclerView
        val layoutManger = LinearLayoutManager(activity)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerHistory?.layoutManager = layoutManger
        _recyclerHistory?.addItemDecoration(SimpleDividerItemDecoration(activity))

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_left.text = "上一頁"
        btn_right.text = "下一頁"
        btn_left.setOnClickListener {
            leftView()
        }

        btn_right.setOnClickListener {
            rightView()
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_history, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun fragmentMenuItemClickListenerObject(): (MenuItem) -> Boolean {
        return { item ->

            when (item.itemId) {
                R.id.menu_backup -> {
                    backUpHistoryData()
                }
            }

            true
        }
    }

    override fun onFragmentBackPressed() {
        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain)
        vpMain.currentItem = 1
    }

    fun setDataModel(model: SensorDataModel) {
        _sensorDataModel = model
    }

    fun renewSensorTitle() {
        tx_sensor1.visibility = _appSettingModel!!.sensorVisibility(0)
        tx_sensor2.visibility = _appSettingModel!!.sensorVisibility(1)
        tx_sensor3.visibility = _appSettingModel!!.sensorVisibility(2)
        tx_sensor4.visibility = _appSettingModel!!.sensorVisibility(3)
        tx_sensor5.visibility = _appSettingModel!!.sensorVisibility(4)
        tx_id.text = getString(R.string.id)
        tx_sensor1.text = _appSettingModel!!.sensorName(0)
        tx_sensor2.text = _appSettingModel!!.sensorName(1)
        tx_sensor3.text = _appSettingModel!!.sensorName(2)
        tx_sensor4.text = _appSettingModel!!.sensorName(3)
        tx_sensor5.text = _appSettingModel!!.sensorName(4)
        tx_time.text = getString(R.string.time)
        tx_title_content.removeAllViewsInLayout()

        if (_appSettingModel!!.sensorQuantity() > 5) {
            tx_title_content.visibility = View.VISIBLE

            for (i in 0 until _appSettingModel!!.sensorQuantity() - 5) {
                val txCustomerTitle = TextView(activity)
                val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (50).toFloat(), activity.resources.displayMetrics)
                txCustomerTitle.text = _appSettingModel!!.sensorName(i + 5)
                txCustomerTitle.visibility = _appSettingModel!!.sensorVisibility(i + 5)
                txCustomerTitle.layoutParams = LinearLayout.LayoutParams(height.toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
                tx_title_content.addView(txCustomerTitle)
            }
        }
        else {
            tx_title_content.visibility = View.GONE
        }

        sensorTitle!!.invalidate()
    }

    fun renewRecyclerView() {
        val model = _sensorDataModel

        when (model.SensorDataLength) {
            in 1 until 100 -> _viewCount = model.SensorDataLength
            0 -> _viewCount = 0
            else -> _viewCount = 100
        }

        _recyclerHistory?.adapter = HistoryDataAdapter(activity, model.SensorDataList, _viewCount)
        checkButton()
    }

    fun setCurrentId(id: String, id2: String) {
        _currentId1 = id
        _currentId2 = id2
    }

    fun setLoadSuccess(isSuccess: Boolean) {
        _isSuccessLoad = isSuccess
    }

    fun getCurrentIdOne(): String {
        return _currentId1
    }

    fun getCurrentIdTwo(): String {
        return _currentId2
    }

    fun getLoadSuccess(): Boolean {
        return _isSuccessLoad
    }

    private fun leftView() {
        (activity as MainActivity).loadHistoryData(this, activity, (_currentId1.toInt() - 100).toString(), (_currentId2.toInt() - 100).toString())
        checkButton()
    }

    private fun rightView() {
        (activity as MainActivity).loadHistoryData(this, activity, (_currentId1.toInt() + 100).toString(), (_currentId2.toInt() + 100).toString())
        checkButton()
    }

    private fun checkButton() {
        if (_viewCount == 0) {
            btn_left.isEnabled = false
            btn_right.isEnabled = false
            btn_deleted.isEnabled = false
        }
        else {
            when (_currentId1 == "1") {
                true -> {
                    btn_left.isEnabled = false
                }
                false -> {
                    btn_left.isEnabled = true
                }
            }

            btn_right.isEnabled = true
            btn_deleted.isEnabled = true
        }
    }

    private fun backUpHistoryData() {
        _historyDataBackUpHelper = HttpHelper.initInstance(activity)
        _historyDataBackUpHelper!!.setHttpAction(object : IHttpAction {
            override fun onHttpRequest() {
                DataWriter.writeData(activity, _appSettingModel!!.fileSavedName()
                        , HttpRequest.downloadFromMySQL("society", PhpUrlDto(activity).LoadingWholeData))
                toast("備份完成")
            }

            override fun onException(e: Exception) {
                Log.e("Backing up history data", e.toString())
                toast(e.toString())
            }

            override fun onPostExecute() {
            }
        })
        _historyDataBackUpHelper!!.startHttpThread()
    }
}