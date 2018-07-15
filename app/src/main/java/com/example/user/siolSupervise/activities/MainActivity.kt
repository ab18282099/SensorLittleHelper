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
        _appSettingModel!!.putString("username", "")

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setContentView(R.layout.activity_main)

        ExitApplication.initInstance()!!.addActivity(this)
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        _activeFragmentList.add(WeakReference(fragment))
    }

    override fun initActionBar() {
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

        setActivityTitle("SOIL SUPERVISE")

        setRightImageAndClickListener(R.drawable.exit, View.OnClickListener {
            alert("你確定要離開?") {
                yesButton { ExitApplication.initInstance()?.exit() }
                noButton { }
            }.show()
        })

        setOnNavigationClickListener({ _ ->
            drawerLayout.openDrawer(Gravity.START)
        })

        setDrawerListener(object : ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {
        })

        setDrawMenuAdapterAndItemClickListener(drawerMenuAdapter, { adapterView, view, i, l ->
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
    override fun findView() {
        val fragmentsTitleList = arrayOf("登入", "主頁", "Wi-Fi遙控", "即時監控", "歷史數據", "折線圖", "設定")
        _fragmentTitleList.addAll(fragmentsTitleList)

        val image = arrayOf(R.drawable.login, R.drawable.main, R.drawable.wifi, R.drawable.current, R.drawable.historydata, R.drawable.chart, R.drawable.setting)
        _fragmentImgList.addAll(image)

        val fragmentsInViewPager = arrayOf(_fragmentLogin, _fragmentMain, _fragmentToggle, _fragmentOnTime, _fragmentHistory, _fragmentChart, _fragmentSetting)
        _fragmentList.addAll(fragmentsInViewPager)

        _vpMain = findViewById<ViewPager>(R.id._vpMain)
    }

    override fun initView() {
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
                        setActivityTitle("登入")
                        setRightImageAndClickListener(R.drawable.exit, View.OnClickListener {
                            alert("你確定要離開?") {
                                yesButton { ExitApplication.initInstance()?.exit() }
                                noButton { }
                            }.show()
                        })
                        removeCallOnTime()
                    }
                    1 -> {
                        val MainFragmentMenuItemClickListener = _mAdapter!!.getFragment(1) as FragmentMenuItemClickListener
                        setActivityTitle("主頁")
                        setRightImageAndClickListener(R.drawable.login, View.OnClickListener {
                            _vpMain?.currentItem = 0
                        })
                        setMenuClickListener(MainFragmentMenuItemClickListener)
                        removeCallOnTime()
                    }
                    2 -> {
                        setActivityTitle("Wi-Fi遙控")
                        setRightImageAndClickListener(R.drawable.main, View.OnClickListener {
                            _vpMain?.currentItem = 1
                        })
                        val toggleFragment = _mAdapter!!.getFragment(2) as ToggleFragment
                        toggleFragment.setToggleRecycler()
                        removeCallOnTime()
                    }
                    3 -> {
                        val onTimeFragment = _mAdapter!!.getFragment(3) as OnTimeFragment
                        setActivityTitle("即時監控")
                        setRightImageAndClickListener(R.drawable.refresh, View.OnClickListener {
                            removeCallOnTime()
                            loadOnTimeData(onTimeFragment)
                        })
                        loadOnTimeData(onTimeFragment)
                    }
                    4 -> {
                        val historyFragment = _mAdapter!!.getFragment(4) as HistoryDataFragment
                        val historyFragmentMenuClickListener = historyFragment as FragmentMenuItemClickListener

                        setActivityTitle("歷史數據")
                        setRightImageAndClickListener(R.drawable.refresh, View.OnClickListener {
                            loadHistoryData(historyFragment, this@MainActivity, historyFragment.getCurrentIdOne(), historyFragment.getCurrentIdTwo())
                        })

                        setMenuClickListener(historyFragmentMenuClickListener)

                        loadHistoryData(historyFragment, this@MainActivity, "1", "100")

                        removeCallOnTime()
                    }
                    5 -> {
                        setActivityTitle("折線圖")
                        setRightImageAndClickListener(R.drawable.main, View.OnClickListener {
                            _vpMain?.currentItem = 1
                        })
                        removeCallOnTime()
                    }
                    6 -> {
                        setActivityTitle("設定")
                        setRightImageAndClickListener(R.drawable.main, View.OnClickListener {
                            _vpMain?.currentItem = 1
                        })
                        removeCallOnTime()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    override fun onBackPressed() {
        val activeFragment = getActiveFragment()
        if (activeFragment.isNotEmpty()) {
            activeFragment.asSequence()
                    .filter { it is FragmentBackPressedListener }
                    .forEach { (it as FragmentBackPressedListener).onFragmentBackPressed() }
        }
        else {
            if (_doubleBackToExit == true && _doubleBackToExit != null) {
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

    override fun onDestroy() {
        if (_httpHelper != null)
            _httpHelper!!.recycleThread()

        super.onDestroy()
    }

    fun loadHistoryData(historyFragment: HistoryDataFragment, context: Context, id1: String, id2: String) {
        _httpHelper = HttpHelper.initInstance(context)
        _httpHelper!!.setHttpAction(object : IHttpAction {
            override fun onHttpRequest() {
                val data = HttpRequest.downloadFromMySQL("society", PhpUrlDto(context).loadingHistoryDataById(id1, id2))
                val model = SensorDataModel()
                val dataParser = SensorDataParser(context)
                model.SensorDataLength = dataParser.getJsonArrayLength(JSONArray(data))
                model.SensorDataList = dataParser.getSensorData(JSONArray(data))

                historyFragment.setCurrentId(id1, id2)
                historyFragment.setDataModel(model)
                historyFragment.setLoadSuccess(true)

                runOnUiThread {
                    historyFragment.renewRecyclerView()
                    historyFragment.renewSensorTitle()
                }
            }

            override fun onException(e: Exception) {
                if (historyFragment.getLoadSuccess()) // if last time is success
                {
                    historyFragment.setLoadSuccess(false) // this time is not success
                    loadHistoryData(historyFragment, context, (id1.toInt() - 100).toString(), (id2.toInt() - 100).toString())
                }
                else {
                    historyFragment.setCurrentId("1", "100")
                    historyFragment.setDataModel(SensorDataModel())
                }

                runOnUiThread { historyFragment.renewRecyclerView() }
            }

            override fun onPostExecute() {
            }
        })
        _httpHelper!!.startHttpThread()
    }

    fun loadOnTimeData(onTimeFragment: OnTimeFragment) {
        _onTimeHandler = Handler()
        _onTimeRunnable = Runnable {
            onTimeFragment.tryLoadLastData()
            _onTimeHandler!!.postDelayed(_onTimeRunnable, 30000)
        }
        _onTimeHandler!!.postDelayed(_onTimeRunnable, 200)
    }

    private fun removeCallOnTime() {
        if (_onTimeHandler != null && _onTimeRunnable != null) {
            _onTimeHandler!!.removeCallbacks(_onTimeRunnable)
        }
    }

    private fun getActiveFragment(): ArrayList<Fragment?> {
        val ret = ArrayList<Fragment?>()
        val weak_ret = _activeFragmentList.asSequence().filter { it.get()!!.isVisible and it.get()!!.userVisibleHint }
        weak_ret.forEach { ret.add(it.get()) }
        return ret
    }
}