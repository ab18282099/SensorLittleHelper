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
import com.example.user.siolSupervise.db.HttpHelper
import com.example.user.siolSupervise.db.IHttpAction
import com.example.user.siolSupervise.dto.PhpUrlDto
import com.example.user.siolSupervise.fragments.*
import com.example.user.siolSupervise.fragments.FragmentBackPressedListener
import com.example.user.siolSupervise.fragments.FragmentMenuItemClickListener
import com.example.user.siolSupervise.models.AppSettingModel
import com.example.user.siolSupervise.models.SensorDataModel
import com.example.user.siolSupervise.R
import com.example.user.siolSupervise.utility.*
import java.lang.ref.WeakReference
import java.util.HashMap
import kotlinx.android.synthetic.main.activity_main.*
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import org.jetbrains.anko.*
import org.json.JSONArray

/**
 * 首頁 Activity
 */
class MainActivity : BaseActivity() {

    // These fragments implement FragmentBackPressedListener, should be "as Fragment"

    /**
     * 登入畫面 fragment
     */
    private val loginFragment = LoginFragment.newInstance() as Fragment

    /**
     * 設定畫面 fragment
     */
    private val settingFragment = SettingFragment.newInstance() as Fragment

    /**
     * 感測器歷史數據清單畫面 fragment
     */
    private val historyViewFragment = HistoryDataFragment.newInstance() as Fragment

    /**
     * 首頁畫面 fragment
     */
    private val mainViewFragment = MainFragment.newInstance() as Fragment

    /**
     * 即時數據畫面 fragment
     */
    private val realTimeViewFragment = RealTimeFragment.newInstance() as Fragment

    /**
     * 感測器圖表畫面 fragment
     */
    private val chartViewFragment = ChartFragment.newInstance() as Fragment

    /**
     * 遙控功能 fragment
     */
    private val remoteControlFragment = RemoteControlFragment.newInstance() as Fragment

    /**
     * 需綁進 ViewPager 的 fragment 清單
     */
    private val fragmentList = ArrayList<Fragment>()

    /**
     * fragment 的標題清單
     */
    private val fragmentTitleList = ArrayList<String>()

    /**
     * fragment 畫面對應圖示 res id 清單
     */
    private val fragmentImageResList = ArrayList<Int>()

    /**
     * HTTP Helper
     */
    private var httpHelper: HttpHelper? = null

    /**
     * 載入 main activity 時所有 attach 的 fragment 弱引用
     */
    private val attachedFragmentList = ArrayList<WeakReference<Fragment?>>()

    /**
     * 主畫面 ViewPager
     */
    private var mainViewPager: ViewPager? = null

    /**
     * Fragment 轉接 ViewPager
     */
    private var fragmentViewPagerAdapter: FragmentViewPagerAdapter? = null

    /**
     * 即時數據畫面排程處理器
     */
    private var onTimeHandler: Handler? = null

    /**
     * 即時數據畫面排程工作
     */
    private var onTimeRunnable: Runnable? = null

    /**
     * 是否為第二次按下 back pressed
     */
    private var isDoubleBackPressed: Boolean = false

    /**
     * 應用程式設定資料模型
     */
    private var appSettingModel: AppSettingModel? = null

    /**
     * Activity onCreate
     * @param savedInstanceState The savedInstanceState is a reference to a Bundle object that is passed into the onCreate method of every Android Activity.
     * Activities have the ability, under special circumstances, to restore themselves to a previous state using the data stored in this bundle.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.appSettingModel = AppSettingModel(this)
        this.appSettingModel!!.putString("username", "")

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        this.setContentView(R.layout.activity_main)

        this.httpHelper = HttpHelper.initInstance(this)
        ExitApplication.useInstance()!!.addActivity(this)
    }

    /**
     * Activity onAttachFragment
     * @param fragment the fragment attached
     */
    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        this.attachedFragmentList.add(WeakReference(fragment))
    }

    /**
     * 初始化 actionBar，綁定相關點擊事件
     */
    override fun initActionBar() {
        val drawerLayout = this.findViewById<DrawerLayout>(R.id.drawerLayout)
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)
        val drawerMenuTextList = arrayOf("關於")
        val drawerMenuIconList = arrayOf(R.drawable.about)
        val items = java.util.ArrayList<Map<String, Any>>()
        for (i in drawerMenuTextList.indices) {
            val item = HashMap<String, Any>()
            item.put("icon", drawerMenuIconList[i])
            item.put("text", drawerMenuTextList[i])
            items.add(item)
        }

        // 建立 drawer menu 的 SimpleAdapter
        val drawerMenuAdapter = SimpleAdapter(
                this,
                items,
                R.layout.item_draw_menu,
                arrayOf("icon", "text"),
                intArrayOf(R.id.imageDrawMenu, R.id.textDrawMenu))

        this.setActivityTitle("SOIL SUPERVISE")

        this.setRightImageAndClickListener(R.drawable.exit, View.OnClickListener {
            this.alert("你確定要離開?") {
                this.yesButton { ExitApplication.useInstance()?.exit() }
                this.noButton { }
            }.show()
        })

        this.setOnNavigationClickListener({ _ ->
            drawerLayout.openDrawer(Gravity.START)
        })

        this.setDrawerListener(object : ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close) {
        })

        this.setDrawMenuAdapterAndItemClickListener(drawerMenuAdapter, { adapterView, view, i, l ->
            when (i) {
                0 -> {
                    this.alert("Create by SHENG-WEI LIN,\nOctober 2017") {
                        this.yesButton { }
                    }.show()
                }
            }
        })
    }

    /**
     * 尋找 MainActivity 的主要布局(fragments & ViewPager)
     */
    override fun findView() {
        val fragmentsTitleList = arrayOf("登入", "主頁", "Wi-Fi遙控", "即時監控", "歷史數據", "折線圖", "設定")
        this.fragmentTitleList.addAll(fragmentsTitleList)

        val images = arrayOf(
                R.drawable.login,
                R.drawable.main,
                R.drawable.wifi,
                R.drawable.current,
                R.drawable.historydata,
                R.drawable.chart,
                R.drawable.setting)
        this.fragmentImageResList.addAll(images)

        val fragmentsInViewPager = arrayOf(
                this.loginFragment,
                this.mainViewFragment,
                this.remoteControlFragment,
                this.realTimeViewFragment,
                this.historyViewFragment,
                this.chartViewFragment,
                this.settingFragment)
        this.fragmentList.addAll(fragmentsInViewPager)

        this.mainViewPager = this.findViewById(R.id._vpMain)
    }

    /**
     * 初始化 MainActivity 布局，綁定 fragment 與 ViewPager，並設定 ViewPager 頁面轉換監聽事件
     */
    override fun initView() {
        this.fragmentViewPagerAdapter = FragmentViewPagerAdapter(this.fragmentManager, this.fragmentList)
        this.mainViewPager?.adapter = this.fragmentViewPagerAdapter

        val commonNavigator = CommonNavigator(this)
        val mNavigatorAdapter = MainCommonNavigatorAdapter(this.fragmentTitleList, this.fragmentImageResList, this.mainViewPager)
        commonNavigator.adapter = mNavigatorAdapter
        this.magic_indicator.navigator = commonNavigator

        // 綁定底部 magic_indicator
        ViewPagerHelper.bind(this.magic_indicator, this.mainViewPager)

        // 設定 ViewPager 頁面轉換監聽事件
        this.mainViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {

                    // 登入頁
                    0 -> {
                        this@MainActivity.setActivityTitle("登入")
                        this@MainActivity.setRightImageAndClickListener(R.drawable.exit, View.OnClickListener {
                            this@MainActivity.alert("你確定要離開?") {
                                this.yesButton { ExitApplication.useInstance()?.exit() }
                                this.noButton { }
                            }.show()
                        })
                        this@MainActivity.removeCallOnTime()
                    }

                    // 主頁圖示選單頁
                    1 -> {
                        val mainFragmentMenuItemClickListener = this@MainActivity.fragmentViewPagerAdapter!!.getFragment(1) as FragmentMenuItemClickListener
                        this@MainActivity.setActivityTitle("主頁")
                        this@MainActivity.setRightImageAndClickListener(R.drawable.login, View.OnClickListener {
                            this@MainActivity.mainViewPager?.currentItem = 0
                        })
                        this@MainActivity.setMenuClickListener(mainFragmentMenuItemClickListener)
                        this@MainActivity.removeCallOnTime()
                    }

                    // 遙控操作頁
                    2 -> {
                        this@MainActivity.setActivityTitle("Wi-Fi遙控")
                        this@MainActivity.setRightImageAndClickListener(R.drawable.main, View.OnClickListener {
                            this@MainActivity.mainViewPager?.currentItem = 1
                        })
                        val toggleFragment = this@MainActivity.fragmentViewPagerAdapter!!.getFragment(2) as RemoteControlFragment
                        toggleFragment.setToggleRecycler()
                        this@MainActivity.removeCallOnTime()
                    }

                    // 即時數據頁
                    3 -> {
                        val realTimeFragment = this@MainActivity.fragmentViewPagerAdapter!!.getFragment(3) as RealTimeFragment
                        this@MainActivity.setActivityTitle("即時監控")
                        this@MainActivity.setRightImageAndClickListener(R.drawable.refresh, View.OnClickListener {
                            this@MainActivity.removeCallOnTime()
                            this@MainActivity.loadOnTimeData(realTimeFragment)
                        })
                        this@MainActivity.loadOnTimeData(realTimeFragment)
                    }

                    // 歷史數據頁
                    4 -> {
                        val historyFragment = this@MainActivity.fragmentViewPagerAdapter!!.getFragment(4) as HistoryDataFragment
                        val historyFragmentMenuClickListener = historyFragment as FragmentMenuItemClickListener

                        this@MainActivity.setActivityTitle("歷史數據")
                        this@MainActivity.setRightImageAndClickListener(R.drawable.refresh, View.OnClickListener {
                            this@MainActivity.loadHistoryData(historyFragment, this@MainActivity, historyFragment.getCurrentIdOne(), historyFragment.getCurrentIdTwo())
                        })

                        this@MainActivity.setMenuClickListener(historyFragmentMenuClickListener)

                        this@MainActivity.loadHistoryData(historyFragment, this@MainActivity, "1", "100")

                        this@MainActivity.removeCallOnTime()
                    }

                    // 數據趨勢圖頁面
                    5 -> {
                        this@MainActivity.setActivityTitle("折線圖")
                        this@MainActivity.setRightImageAndClickListener(R.drawable.main, View.OnClickListener {
                            this@MainActivity.mainViewPager?.currentItem = 1
                        })
                        this@MainActivity.removeCallOnTime()
                    }

                    // 應用程式設定頁
                    6 -> {
                        this@MainActivity.setActivityTitle("設定")
                        this@MainActivity.setRightImageAndClickListener(R.drawable.main, View.OnClickListener {
                            this@MainActivity.mainViewPager?.currentItem = 1
                        })
                        this@MainActivity.removeCallOnTime()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    /**
     * 返回鍵點擊監聽事件
     */
    override fun onBackPressed() {
        val activeFragment = this.getActiveFragment()
        if (activeFragment.isNotEmpty()) {
            activeFragment.asSequence()
                    .filter { it is FragmentBackPressedListener }
                    .forEach { (it as FragmentBackPressedListener).onFragmentBackPressed() }
        }
        else {
            if (this.isDoubleBackPressed) {
                ExitApplication.useInstance()?.exit()
                return
            }
            this.isDoubleBackPressed = true
            this.toast("Press Back again to exit")

            Handler().postDelayed({
                this.isDoubleBackPressed = false
            }, 1000)
        }
    }

    /**
     * activity onDestroy，在此回收 HttpHelper 的 HandlerThread
     */
    override fun onDestroy() {

        // 回收 httpHelper 執行序
        if (this.httpHelper != null)
            this.httpHelper!!.recycleThread()

        super.onDestroy()
    }

    /**
     * 下載歷史數據
     * @param historyFragment 歷史清單頁 fragment
     * @param context 當前 app context
     * @param idOne 欲下載的資料編號起始
     * @param idTwo 欲下載的資料編號結尾
     */
    fun loadHistoryData(historyFragment: HistoryDataFragment, context: Context, idOne: String, idTwo: String) {

        this.httpHelper!!.setHttpAction(object : IHttpAction {
            override fun onHttpRequest() {

                // 下載資料並進行轉檔
                val data = HttpRequest.downloadFromMySQL("society", PhpUrlDto(context).loadingHistoryDataById(idOne, idTwo))
                val model = SensorDataModel()
                val dataParser = SensorDataParser(context)
                model.SensorDataLength = dataParser.getJsonArrayLength(JSONArray(data))
                model.SensorDataList = dataParser.getSensorData(JSONArray(data))

                historyFragment.setCurrentId(idOne, idTwo)
                historyFragment.setDataModel(model)
                historyFragment.setLoadSuccess(true)

                // 完成資料載入後於 ui 執行序刷新畫面
                this@MainActivity.runOnUiThread {
                    historyFragment.renewRecyclerView()
                    historyFragment.renewSensorTitle()
                }
            }

            override fun onException(e: Exception) {

                // if last time is success
                if (historyFragment.getLoadSuccess())
                {
                    // this time is not success
                    historyFragment.setLoadSuccess(false)
                    this@MainActivity.loadHistoryData(historyFragment, context, (idOne.toInt() - 100).toString(), (idTwo.toInt() - 100).toString())
                }
                else {
                    historyFragment.setCurrentId("1", "100")
                    historyFragment.setDataModel(SensorDataModel())
                }

                this@MainActivity.runOnUiThread { historyFragment.renewRecyclerView() }
            }

            override fun onPostExecute() {
            }
        })

        this.httpHelper!!.startHttpThread()
    }

    /**
     * 載入即時數據資料
     * @param realTimeFragment 即時數據頁面 fragment
     */
    private fun loadOnTimeData(realTimeFragment: RealTimeFragment) {
        this.onTimeHandler = Handler()
        this.onTimeRunnable = Runnable {
            realTimeFragment.tryLoadLastData()
            this.onTimeHandler!!.postDelayed(this.onTimeRunnable, 30000)
        }

        this.onTimeHandler!!.postDelayed(this.onTimeRunnable, 200)
    }

    /**
     * 移除即時數據載入工作排成
     */
    private fun removeCallOnTime() {
        if (this.onTimeHandler != null && this.onTimeRunnable != null) {
            this.onTimeHandler!!.removeCallbacks(this.onTimeRunnable)
        }
    }

    /**
     * 取得當前顯示頁面的 fragment
     * @return 當前頁面 fragment
     */
    private fun getActiveFragment(): ArrayList<Fragment?> {
        val ret = ArrayList<Fragment?>()
        this.attachedFragmentList
                .asSequence()
                .filter { it.get()!!.isVisible and it.get()!!.userVisibleHint }
                .forEach { ret.add(it.get()) }

        return ret
    }
}