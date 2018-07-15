package com.example.user.siolSupervise.activities

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.SimpleAdapter
import com.example.user.siolSupervise.fragments.*
import com.example.user.siolSupervise.db.HttpHelper
import com.example.user.siolSupervise.fragments.FragmentBackPressedListener
import com.example.user.siolSupervise.fragments.FragmentMenuItemClickListener
import com.example.user.siolSupervise.models.AppSettingModel
import com.example.user.siolSupervise.dto.PhpUrlDto
import com.example.user.siolSupervise.models.SensorDataModel
import com.example.user.siolSupervise.db.IHttpAction
import com.example.user.siolSupervise.utility.*
import com.example.user.siolSupervise.R
import kotlinx.android.synthetic.main.activity_main.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import org.jetbrains.anko.*
import org.json.JSONArray
import java.lang.ref.WeakReference
import java.util.HashMap

class MainActivity : BaseActivity() {
    private val _fragmentLogin = LoginFragment.NewInstance() as Fragment //just test for ssd
    private val _fragmentSetting = SettingFragment.NewInstance() as Fragment // These fragments implement FragmentBackPressedListener, should be "as Fragment"
    private val _fragmentHistory = HistoryDataFragment.NewInstance() as Fragment
    private val _fragmentMain = MainFragment.NewInstance() as Fragment
    private val _fragmentOnTime = OnTimeFragment.NewInstance() as Fragment
    private val _fragmentChart = ChartFragment.NewInstance() as Fragment
    private val _fragmentToggle = ToggleFragment.NewInstance() as Fragment
    private val _fragmentList = ArrayList<Fragment>()
    private val _fragmentTitleList = ArrayList<String>()
    private val _fragmentImgList = ArrayList<Int>()
    private var _httpHelper: HttpHelper? = null
    private val _activeFragmentList = ArrayList<WeakReference<Fragment?>>()
    private var _vpMain: ViewPager? = null
    private var _mAdapter: FragmentViewPagerAdapter? = null
    private var _onTimeHandler: Handler? = null
    private var _onTimeRunnable: Runnable? = null
    private var _doubleBackToExit: Boolean? = null
    private var _appSettingModel: AppSettingModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _appSettingModel = AppSettingModel(this)
        _appSettingModel!!.PutString("Username", "")

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(R.layout.activity_main)

        ExitApplication.InitInstance()!!.AddActivity(this)
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        _activeFragmentList.add(WeakReference(fragment))
    }

    override fun InitActionBar() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val drawerMenuTextList = arrayOf("關於")
        val drawerMenuIconList = arrayOf(R.drawable.about)
        val items = java.util.ArrayList<Map<String, Any>>()
        for (i in drawerMenuTextList.indices) {
            val item = HashMap<String, Any>()
            item.put("icon", drawerMenuIconList[i])
            item.put("text", drawerMenuTextList[i])
            items.add(item)
        }
        val drawerMenuAdapter = SimpleAdapter(this, items, R.layout.item_draw_menu,
                arrayOf("icon", "text"), intArrayOf(R.id.imageDrawMenu, R.id.textDrawMenu))

        SetActivityTitle("SOIL SUPERVISE")

        SetRightImageAndClickListener(R.drawable.exit, View.OnClickListener {
            alert("你確定要離開?") {
                yesButton { ExitApplication.InitInstance()?.Exit() }
                noButton { }
            }.show()
        })

        SetOnNavigationClickListener({ _ ->
            drawerLayout.openDrawer(Gravity.START)
        })

        SetDrawerListener(object : ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {
        })

        SetDrawMenuAdapterAndItemClickListener(drawerMenuAdapter, { adapterView, view, i, l ->
            when (i) {
                0 -> {
                    alert("Create by SHENG-WEI LIN,\nOctober 2017") {
                        yesButton { }
                    }.show()
                }
            }
        })
    }

    //After onCreate(in baseActivity) add Fragment
    override fun FindView() {
        val fragmentsTitleList = arrayOf("登入", "主頁", "Wi-Fi遙控", "即時監控", "歷史數據", "折線圖", "設定")
        _fragmentTitleList.addAll(fragmentsTitleList)

        val image = arrayOf(R.drawable.login, R.drawable.main, R.drawable.wifi, R.drawable.current, R.drawable.historydata, R.drawable.chart, R.drawable.setting)
        _fragmentImgList.addAll(image)

        val fragmentsInViewPager = arrayOf(_fragmentLogin, _fragmentMain, _fragmentToggle, _fragmentOnTime, _fragmentHistory, _fragmentChart, _fragmentSetting)
        _fragmentList.addAll(fragmentsInViewPager)

        _vpMain = findViewById<ViewPager>(R.id._vpMain)
    }

    override fun InitView() {
        _mAdapter = FragmentViewPagerAdapter(fragmentManager, _fragmentList)
        _vpMain?.adapter = _mAdapter

        val commonNavigator = CommonNavigator(this)
        val mNavigatorAdapter = MyCommonNavigatorAdapter(_fragmentTitleList, _fragmentImgList, _vpMain)
        commonNavigator.adapter = mNavigatorAdapter
        magic_indicator.navigator = commonNavigator

        ViewPagerHelper.bind(magic_indicator, _vpMain)

        //USE "object" to init component
        _vpMain?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        SetActivityTitle("登入")
                        SetRightImageAndClickListener(R.drawable.exit, View.OnClickListener {
                            alert("你確定要離開?") {
                                yesButton { ExitApplication.InitInstance()?.Exit() }
                                noButton { }
                            }.show()
                        })
                        RemoveCallOnTime()
                    }
                    1 -> {
                        val MainFragmentMenuItemClickListener = _mAdapter!!.GetFragment(1) as FragmentMenuItemClickListener
                        SetActivityTitle("主頁")
                        SetRightImageAndClickListener(R.drawable.login, View.OnClickListener {
                            _vpMain?.currentItem = 0
                        })
                        SetMenuClickListener(MainFragmentMenuItemClickListener)
                        RemoveCallOnTime()
                    }
                    2 -> {
                        SetActivityTitle("Wi-Fi遙控")
                        SetRightImageAndClickListener(R.drawable.main, View.OnClickListener {
                            _vpMain?.currentItem = 1
                        })
                        val toggleFragment = _mAdapter!!.GetFragment(2) as ToggleFragment
                        toggleFragment.SetToggleRecycler()
                        RemoveCallOnTime()
                    }
                    3 -> {
                        val onTimeFragment = _mAdapter!!.GetFragment(3) as OnTimeFragment
                        SetActivityTitle("即時監控")
                        SetRightImageAndClickListener(R.drawable.refresh, View.OnClickListener {
                            RemoveCallOnTime()
                            LoadOnTimeData(onTimeFragment)
                        })
                        LoadOnTimeData(onTimeFragment)
                    }
                    4 -> {
                        val historyFragment = _mAdapter!!.GetFragment(4) as HistoryDataFragment
                        val historyFragmentMenuClickListener = historyFragment as FragmentMenuItemClickListener

                        SetActivityTitle("歷史數據")
                        SetRightImageAndClickListener(R.drawable.refresh, View.OnClickListener {
                            LoadHistoryData(historyFragment, this@MainActivity, historyFragment.GetCurrentId1(), historyFragment.GetCurrentId2())
                        })

                        SetMenuClickListener(historyFragmentMenuClickListener)

                        LoadHistoryData(historyFragment, this@MainActivity, "1", "100")

                        RemoveCallOnTime()
                    }
                    5 -> {
                        SetActivityTitle("折線圖")
                        SetRightImageAndClickListener(R.drawable.main, View.OnClickListener {
                            _vpMain?.currentItem = 1
                        })
                        RemoveCallOnTime()
                    }
                    6 -> {
                        SetActivityTitle("設定")
                        SetRightImageAndClickListener(R.drawable.main, View.OnClickListener {
                            _vpMain?.currentItem = 1
                        })
                        RemoveCallOnTime()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    override fun onBackPressed() {
        val activeFragment = GetActiveFragment()
        if (activeFragment.isNotEmpty()) {
            activeFragment.asSequence()
                    .filter { it is FragmentBackPressedListener }
                    .forEach { (it as FragmentBackPressedListener).OnFragmentBackPressed() }
        }
        else {
            if (_doubleBackToExit == true && _doubleBackToExit != null) {
                ExitApplication.InitInstance()?.Exit()
                return
            }
            this._doubleBackToExit = true
            toast("Press Back again to Exit")

            Handler().postDelayed({
                _doubleBackToExit = false
            }, 1000)
        }
    }

    override fun onDestroy() {
        if (_httpHelper != null)
            _httpHelper!!.RecycleThread()

        super.onDestroy()
    }

    fun LoadHistoryData(historyFragment: HistoryDataFragment, context: Context, id1: String, id2: String) {
        _httpHelper = HttpHelper.InitInstance(context)
        _httpHelper!!.SetHttpAction(object : IHttpAction {
            override fun OnHttpRequest() {
                val data = HttpRequest.DownloadFromMySQL("society", PhpUrlDto(context).LoadingHistoryDataById(id1, id2))
                val model = SensorDataModel()
                val dataParser = SensorDataParser(context)
                model.SensorDataLength = dataParser.GetJsonArrayLength(JSONArray(data))
                model.SensorDataList = dataParser.GetSensorData(JSONArray(data))

                historyFragment.SetCurrentId(id1, id2)
                historyFragment.SetDataModel(model)
                historyFragment.SetLoadSuccess(true)

                runOnUiThread {
                    historyFragment.RenewRecyclerView()
                    historyFragment.RenewSensorTitle()
                }
            }

            override fun OnException(e: Exception) {
                if (historyFragment.GetLoadSuccess()) // if last time is success
                {
                    historyFragment.SetLoadSuccess(false) // this time is not success
                    LoadHistoryData(historyFragment, context, (id1.toInt() - 100).toString(), (id2.toInt() - 100).toString())
                }
                else {
                    historyFragment.SetCurrentId("1", "100")
                    historyFragment.SetDataModel(SensorDataModel())
                }

                runOnUiThread { historyFragment.RenewRecyclerView() }
            }

            override fun OnPostExecute() {
            }
        })
        _httpHelper!!.StartHttpThread()
    }

    fun LoadOnTimeData(onTimeFragment: OnTimeFragment) {
        _onTimeHandler = Handler()
        _onTimeRunnable = Runnable {
            onTimeFragment.TryLoadLastData()
            _onTimeHandler!!.postDelayed(_onTimeRunnable, 30000)
        }
        _onTimeHandler!!.postDelayed(_onTimeRunnable, 200)
    }

    private fun RemoveCallOnTime() {
        if (_onTimeHandler != null && _onTimeRunnable != null) {
            _onTimeHandler!!.removeCallbacks(_onTimeRunnable)
        }
    }

    private fun GetActiveFragment(): ArrayList<Fragment?> {
        val ret = ArrayList<Fragment?>()
        val weak_ret = _activeFragmentList.asSequence().filter { it.get()!!.isVisible and it.get()!!.userVisibleHint }
        weak_ret.forEach { ret.add(it.get()) }
        return ret
    }
}