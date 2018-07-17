package com.example.user.siolSupervise.activities

import android.graphics.Color
import android.os.Bundle
import android.support.annotation.*
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.*
import com.example.user.siolSupervise.fragments.FragmentMenuItemClickListener
import com.example.user.siolSupervise.R

/**
 * Activity 基底類別
 */
abstract class BaseActivity : AppCompatActivity() {

    /**
     * Activity 底層布局
     */
    private var contentView: LinearLayout? = null

    /**
     * 左側 drawer
     */
    private var drawerLayout: DrawerLayout? = null

    /**
     * 左側 drawer 清單布局
     */
    private var drawerMenuListView: ListView? = null

    /**
     * 頂部工具列
     */
    private var toolbar: Toolbar? = null

    /**
     * 頂部工具列標題
     */
    private var toolBarTitle: TextView? = null

    /**
     * 工具列右側圖示
     */
    private var toolbarImage: ImageView? = null

    /**
     * 導覽列點擊監聽
     */
    private var onNavigationClickListener: ((View) -> Unit)? = null

    /**
     * Activity onCreate
     * @param savedInstanceState The savedInstanceState is a reference to a Bundle object that is passed into the onCreate method of every Android Activity.
     * Activities have the ability, under special circumstances, to restore themselves to a previous state using the data stored in this bundle.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_base)
        Log.e("baseActivity", "onCreate")
    }

    /**
     * Activity setContentView
     * @param layoutResId 底層布局的畫面 res 編號
     */
    override fun setContentView(@LayoutRes layoutResId: Int) {

        // for base activity and drawerLayout
        if (this.contentView == null && R.layout.activity_base == layoutResId)
        {
            super.setContentView(R.layout.activity_base)

            this.contentView = this.findViewById(R.id.layout_center)
            this.drawerLayout = this.findViewById(R.id.drawerLayout)
            this.drawerMenuListView = this.findViewById(R.id.listDrawerMenu)
            this.toolBarTitle = this.findViewById(R.id.toolbar_title)
            this.toolbarImage = this.findViewById(R.id._toolbarImage)

            this.contentView!!.removeAllViews()
        }

        // for other activity
        else if (layoutResId != R.layout.activity_base)
        {
            val nullViewGroup: ViewGroup? = null
            val addView = LayoutInflater.from(this).inflate(layoutResId, nullViewGroup)
            this.contentView!!.addView(addView, ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT))

            // do not change sequence
            this.findView()
            this.initView()

            // do not change sequence
            this.beforeSetActionBar()
            this.initActionBar()
            this.afterSetActionBar()
        }
        Log.e("baseActivity", "setContentView")
    }

    /**
     * 設定 toolbar 選單點擊事件監聽
     * @param onMenuItemClickListener 監聽器
     */
    fun setMenuClickListener(onMenuItemClickListener: FragmentMenuItemClickListener) {
        this.toolbar!!.setOnMenuItemClickListener(onMenuItemClickListener.fragmentMenuItemClickListenerObject())
    }

    /**
     * 設定當前 Activity 標題
     * @param text 標題
     */
    fun setActivityTitle(text: String) {
        this.toolBarTitle!!.text = text
    }

    /**
     * 設定工具列右邊圖片點擊事件監聽
     * @param imageId 圖片 res 編號
     * @param listener 監聽器
     */
    fun setRightImageAndClickListener(@DrawableRes imageId: Int, listener: View.OnClickListener) {
        this.toolbarImage!!.setImageDrawable(ContextCompat.getDrawable(this, imageId))
        this.toolbarImage!!.setOnClickListener(listener)
    }

    /**
     * 設定導覽列點擊事件
     * @param onClickListener 監聽器
     */
    fun setOnNavigationClickListener(onClickListener: (View) -> Unit) {
        this.onNavigationClickListener = onClickListener
    }

    /**
     * 設定 drawer view toggle 監聽
     * @param drawerToggle
     * class provides a handy way to tie together the functionality of
     * DrawerLayout and the framework <code>ActionBar</code> to
     * implement the recommended design for navigation drawers.
     */
    fun setDrawerListener(drawerToggle: ActionBarDrawerToggle) {

        // Synchronize the state of the drawer indicator/affordance with the linked DrawerLayout.
        drawerToggle.syncState()
        this.drawerLayout!!.addDrawerListener(drawerToggle)
    }

    /**
     * 設定 draw view 內畫面選單轉接器與點擊監聽
     * @param adapter 轉接器
     * @param itemClickListener 監聽器
     */
    fun setDrawMenuAdapterAndItemClickListener(adapter: SimpleAdapter,
                                               itemClickListener: (AdapterView<*>?, View?, Int, Long) -> Unit) {
        this.drawerMenuListView!!.adapter = adapter
        this.drawerMenuListView!!.setOnItemClickListener(itemClickListener)
    }

    /**
     * 設定 actionBar(toolbar) 前呼叫
     */
    private fun beforeSetActionBar() {
        this.toolbar = this.findViewById(R.id.toolbar) as Toolbar
        this.toolbar!!.setTitleTextColor(Color.WHITE)
        this.toolbar!!.title = ""
        this.toolbar!!.isEnabled = true
    }

    //abstract fun initActionBar

    /**
     * actionBar(toolbar) 設定完成後呼叫
     */
    private fun afterSetActionBar() {
        this.setSupportActionBar(this.toolbar)

        if (this.supportActionBar != null) {
            this.supportActionBar!!.setDisplayShowTitleEnabled(false)

            // use DrawerLayout
            this.supportActionBar!!.setDisplayShowHomeEnabled(true)
            this.supportActionBar!!.setHomeButtonEnabled(true)
            this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        this.toolbar!!.setNavigationOnClickListener(this.onNavigationClickListener)
    }

    /**
     * 抽象方法，初始化 actionBar(toolbar)
     */
    abstract fun initActionBar()

    /**
     * 抽象方法，尋找布局
     */
    abstract fun findView()

    /**
     * 抽象方法，初始化布局
     */
    abstract fun initView()
}