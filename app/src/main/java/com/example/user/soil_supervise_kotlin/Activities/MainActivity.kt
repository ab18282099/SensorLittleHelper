package com.example.user.soil_supervise_kotlin.Activities

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.example.user.soil_supervise_kotlin.Fragments.*
import com.example.user.soil_supervise_kotlin.Interfaces.FragmentBackPressedListener
import com.example.user.soil_supervise_kotlin.OtherClass.*
import com.example.user.soil_supervise_kotlin.R
import kotlinx.android.synthetic.main.activity_main.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.json.JSONArray
import java.lang.ref.WeakReference

class MainActivity : BaseActivity()
{

    private val _fragmentLogin = LoginFragment.newInstance() as Fragment //just test for ssd
    private val _fragmentSetting = SettingFragment.newInstance() as Fragment // These fragments implement FragmentBackPressedListener, should be "as Fragment"
    private val _fragmentHistory = HistoryDataFragment.newInstance() as Fragment
    private val _fragmentMain = MainFragment.newInstance() as Fragment
    private val _fragmentOnTime = OnTimeFragment.newInstance() as Fragment
    private val _fragmentChart = ChartFragment.newInstance() as Fragment
    private val _fragmentToggle = ToggleFragment.newInstance() as Fragment

    private val _fragmentList = ArrayList<Fragment>()
    private val _fragmentTitleList = ArrayList<String>()
    private val _fragmentImgList = ArrayList<Int>()

    private val _activeFragmentList = ArrayList<WeakReference<Fragment?>>()
    private var _vpMain: ViewPager? = null
    private var _mAdapter: FragmentViewPagerAdapter? = null

    private var _httpThread: HandlerThread? = null
    private var _threadHandler: Handler? = null

    private var _onTimeHandler: Handler? = null
    private var _onTimeRunnable: Runnable? = null

    private var _doubleBackToExit: Boolean? = null

    private var _sharePref: MySharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        _sharePref = MySharedPreferences.initInstance(this)
        _sharePref!!.PutString("getUser", "")

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(R.layout.activity_main)

        Log.e("MainActivity", "onCreate")

        ExitApplication.initInstance()!!.addActivity(this)
    }

    override fun onAttachFragment(fragment: Fragment?)
    {
        super.onAttachFragment(fragment)
        _activeFragmentList.add(WeakReference(fragment))
        Log.e("MainActivity", "onAttachFragment")
    }

    override fun onStart()
    {
        super.onStart()
        Log.e("MainActivity", "onStart")
    }

    override fun onResume()
    {
        super.onResume()
        Log.e("MainActivity", "onResume")
    }

    override fun onPause()
    {
        super.onPause()
        Log.e("MainActivity", "onPause")
    }

    override fun onStop()
    {
        super.onStop()
        Log.e("MainActivity", "onStop")
    }

    override fun onRestart()
    {
        super.onRestart()
        Log.e("MainActivity", "onRestart")
    }

    //After onCreate(in baseActivity) add Fragment
    override fun findView()
    {
        val fragmentsTitleList = arrayOf("登入", "主頁", "Wi-Fi遙控", "即時監控", "歷史數據", "折線圖", "設定")
        _fragmentTitleList.addAll(fragmentsTitleList)

        val image = arrayOf(R.drawable.login, R.drawable.main, R.drawable.wifi, R.drawable.current, R.drawable.historydata, R.drawable.chart, R.drawable.setting)
        _fragmentImgList.addAll(image)

        val fragmentsInViewPager = arrayOf(_fragmentLogin, _fragmentMain, _fragmentToggle, _fragmentOnTime, _fragmentHistory, _fragmentChart, _fragmentSetting)
        _fragmentList.addAll(fragmentsInViewPager)

        _vpMain = findViewById<ViewPager>(R.id._vpMain) as ViewPager
    }

    override fun initActionBar()
    {
        setActivityTitle("SOIL SUPERVISE")
//        setRightTextAndClickListener("MAIN", View.OnClickListener {
//            _vpMain?.currentItem = 0
//        })
        setRightImageAndClickListener(R.drawable.exit, View.OnClickListener {
            alert("你確定要離開?") {
                yesButton { ExitApplication.initInstance()?.exit() }
                noButton { }
            }.show()
        })
        setOnNavigationClickListener(View.OnClickListener {

        })

        setMenu(R.menu.menu_main, { item ->

            when (item.itemId)
            {
                R.id.menu_login ->
                {
                    _vpMain?.currentItem = 0
                }
                R.id.menu_main ->
                {
                    _vpMain?.currentItem = 1
                }
                R.id.menu_wifield ->
                {
                    _vpMain?.currentItem = 2
                }
                R.id.menu_current ->
                {
                    _vpMain?.currentItem = 3
                }
                R.id.menu_history ->
                {
                    _vpMain?.currentItem = 4
                }
                R.id.menu_chart ->
                {
                    _vpMain?.currentItem = 5
                }
                R.id.menu_mainSet ->
                {
                    _vpMain?.currentItem = 6
                }
                R.id.menu_mainExit ->
                {
                    alert("你確定要離開?") {
                        yesButton { ExitApplication.initInstance()?.exit() }
                        noButton { }
                    }.show()
                }
            }

            true
        })
    }

    override fun initView()
    {
        _mAdapter = FragmentViewPagerAdapter(fragmentManager, _fragmentList)
        _vpMain?.adapter = _mAdapter

        val commonNavigator = CommonNavigator(this)
        val mNavigatorAdapter = MyCommonNavigatorAdapter(_fragmentTitleList, _fragmentImgList, _vpMain)
        commonNavigator.adapter = mNavigatorAdapter
        magic_indicator.navigator = commonNavigator

        ViewPagerHelper.bind(magic_indicator, _vpMain)

        //USE "object" to init component
        _vpMain?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener
        {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
            {

            }

            override fun onPageSelected(position: Int)
            {

                when (position)
                {
                    0 ->
                    {
                        setActivityTitle("登入")
                        setRightImageAndClickListener(R.drawable.exit, View.OnClickListener {
                            alert("你確定要離開?") {
                                yesButton { ExitApplication.initInstance()?.exit() }
                                noButton { }
                            }.show()
                        })
                        removeCallOnTime()
                    }
                    1 ->
                    {
                        setActivityTitle("主頁")
                        setRightImageAndClickListener(R.drawable.login, View.OnClickListener {
                            _vpMain?.currentItem = 0
                        })
                        _recycleThread()
                        removeCallOnTime()
                    }
                    2 ->
                    {
                        setActivityTitle("Wi-Fi遙控")
                        setRightImageAndClickListener(R.drawable.main, View.OnClickListener {
                            _vpMain?.currentItem = 1
                        })
                        val toggleFragment = _mAdapter!!.getFragment(2) as ToggleFragment
                        toggleFragment.setSensorQuantity(_sharePref!!.getSensorQuantity())
                        toggleFragment.setToggleRecycler()
                        _recycleThread()
                        removeCallOnTime()
                    }
                    3 ->
                    {
                        val onTimeFragment = _mAdapter!!.getFragment(3) as OnTimeFragment
                        setActivityTitle("即時監控")
                        setRightImageAndClickListener(R.drawable.refresh, View.OnClickListener {
                            removeCallOnTime()
                            loadOnTimeData(onTimeFragment)
                        })
                        loadOnTimeData(onTimeFragment)
                        _recycleThread()
                    }
                    4 ->
                    {
                        val historyFragment = _mAdapter!!.getFragment(4) as HistoryDataFragment
                        setActivityTitle("歷史數據")
                        setRightImageAndClickListener(R.drawable.refresh, View.OnClickListener {
                            loadHistoryData(historyFragment, this@MainActivity, historyFragment.getCurrentId1(), historyFragment.getCurrentId2())
                        })
                        loadHistoryData(historyFragment, this@MainActivity, "1", "100")
                        removeCallOnTime()
                    }
                    5 ->
                    {
                        setActivityTitle("折線圖")
                        setRightImageAndClickListener(R.drawable.main, View.OnClickListener {
                            _vpMain?.currentItem = 1
                        })
                        _recycleThread()
                        removeCallOnTime()
                    }
                    6 ->
                    {
                        setActivityTitle("設定")
                        setRightImageAndClickListener(R.drawable.main, View.OnClickListener {
                            _vpMain?.currentItem = 1
                        })
                        _recycleThread()
                        removeCallOnTime()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int)
            {

            }
        })
    }

    override fun onBackPressed()
    {
        Log.e("MainActivity", "onBackPressed")

        removeCallOnTime()

        val activeFragment = _getActiveFragment()
        if (activeFragment.isNotEmpty())
        {
            activeFragment.asSequence().filter { it is FragmentBackPressedListener }.forEach { (it as FragmentBackPressedListener).OnFragmentBackPressed() }
        }
        else
        {
            if (_doubleBackToExit == true && _doubleBackToExit != null)
            {
                ExitApplication.initInstance()?.exit()
                return
            }
            this._doubleBackToExit = true
            toast("Press Back again to exit")

            Handler().postDelayed({
                _doubleBackToExit = false
            }, 1000)
        }
    }

    override fun onDestroy()
    {
        Log.e("MainActivity", "onDestroy")

        _recycleThread()

        super.onDestroy()
    }

    private fun _getActiveFragment(): ArrayList<Fragment?>
    {
        val ret = ArrayList<Fragment?>()
        val weak_ret = _activeFragmentList.asSequence().filter { it.get()!!.isVisible and it.get()!!.userVisibleHint }
        weak_ret.forEach { ret.add(it.get()) }
        return ret
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

    fun loadHistoryData(historyFragment: HistoryDataFragment, context: Context, id: String, id2: String)
    {
        val progressDialog = ProgressDialog.dialogProgress(context, "連接中…", View.VISIBLE)
        progressDialog.show()
        progressDialog.setCancelable(false)

        val sharePref = MySharedPreferences.initInstance(context)

        val serverIP = sharePref!!.getServerIP()
        val user = sharePref.getUser()
        val pass = sharePref.getPass()

        _httpThread = HandlerThread("history_data_download")
        _httpThread!!.start()
        _threadHandler = Handler(_httpThread!!.looper)
        _threadHandler!!.post {
            try
            {
                val phpAddress = "http://$serverIP/load_history.php?&server=$serverIP&user=$user&pass=$pass&id=$id&id2=$id2"
                val data = HttpRequest.executeQuery("society", phpAddress)

                val sensorQuantity = sharePref.getSensorQuantity()
                val sensorDataAnalyser = SensorDataAnalyser.initInstance()
                sensorDataAnalyser!!.setSensorQuantity(sensorQuantity)

                sensorDataAnalyser.setSharePref(sharePref)

                historyFragment.setCurrentId(id, id2)

                val dataList = sensorDataAnalyser.getSensorData(JSONArray(data)) //Json Exception catch by this line
                val dateLength = sensorDataAnalyser.getJsonArrayLength(JSONArray(data))

                historyFragment.setSensorQuantity(sensorQuantity)

                if (dataList.isNotEmpty())
                    historyFragment.setSensorDataList(dataList)
                if (dateLength != 0)
                    historyFragment.setJsonArrayLength(dateLength)

                runOnUiThread {
                    DataWriter.writeData(context, sharePref.getFileSavedName(), data)
                    historyFragment.renewRecyclerView()
                    historyFragment.setSensorTitle()
                }

                progressDialog.dismiss()

                historyFragment.setLoadSuccess(true)
            }
            catch (e: Exception)
            {
                Log.e("DataDownloadFailed", e.toString())

                progressDialog.dismiss()

                if (historyFragment.getLoadSuccess()) // if last time is success
                {
                    historyFragment.setLoadSuccess(false) // this time is not success
                    loadHistoryData(historyFragment, context, (id.toInt() - 100).toString(), (id2.toInt() - 100).toString())
                }
                else
                {
                    runOnUiThread {
                        historyFragment.setCurrentId("1", "100")
                        historyFragment.setSensorDataList(ArrayList()) // empty list
                        historyFragment.setJsonArrayLength(0)
                        historyFragment.renewRecyclerView()
                    }
                }
            }
        }
    }

    fun loadOnTimeData(onTimeFragment: OnTimeFragment)
    {
        _onTimeHandler = Handler()
        _onTimeRunnable = Runnable {
            onTimeFragment.setSensorQuantity(_sharePref!!.getSensorQuantity())
            onTimeFragment.tryLoadLastData(_sharePref!!.getUser(), _sharePref!!.getPass())
            _onTimeHandler!!.postDelayed(_onTimeRunnable, 30000)
        }
        _onTimeHandler!!.postDelayed(_onTimeRunnable, 500)
    }

    fun removeCallOnTime()
    {
        if (_onTimeHandler != null && _onTimeRunnable != null)
        {
            _onTimeHandler!!.removeCallbacks(_onTimeRunnable)
        }
    }
}