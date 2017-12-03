package com.example.user.soil_supervise_kotlin.Activities

import android.app.Fragment
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
import com.example.user.soil_supervise_kotlin.Fragments.FragmentMenuItemClickListener
import com.example.user.soil_supervise_kotlin.R

abstract class BaseActivity : AppCompatActivity()
{
    val TAG: String = this.javaClass.simpleName
    private var _contentView: LinearLayout? = null //layout_center
    private var _mDrawerLayout: DrawerLayout? = null
    private var _drawerMenuListView: ListView? = null
    private var _mToolbar: Toolbar? = null
    private var _toolBarTitle: TextView? = null
    private var _toolbarImage: ImageView? = null

    private var _onMenuItemClickListener: ((MenuItem) -> Boolean)? = null
    private var _onNavigationClickListener: ((View) -> Unit)? = null
    private val _invalidMenu = -1
    private var _menuRes = _invalidMenu

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        Log.e("baseActivity", "onCreate")
    }

    override fun setContentView(@LayoutRes layoutResID: Int)
    {
        if (_contentView == null && R.layout.activity_base == layoutResID) // for base activity and drawerLayout
        {
            super.setContentView(R.layout.activity_base)

            _contentView = findViewById<LinearLayout>(R.id.layout_center) as LinearLayout
            _mDrawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout) as DrawerLayout
            _drawerMenuListView = findViewById<ListView>(R.id.listDrawerMenu) as ListView
            _toolBarTitle = findViewById<TextView>(R.id.toolbar_title) as TextView
            _toolbarImage = findViewById<ImageView>(R.id._toolbarImage) as ImageView

            _contentView!!.removeAllViews()
        }
        else if (layoutResID != R.layout.activity_base) // for other activity
        {
            val nullViewGroup: ViewGroup? = null
            val addView = LayoutInflater.from(this).inflate(layoutResID, nullViewGroup)
            _contentView!!.addView(addView, ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT))

            //do not change sequence
            FindView()
            InitView()

            //do not change sequence
            BeforeSetActionBar()
            InitActionBar()
            AfterSetActionBar()
        }
        Log.e("baseActivity", "setContentView")
    }

    override fun onAttachFragment(fragment: Fragment?)
    {
        super.onAttachFragment(fragment)
        Log.e("baseActivity", "onAttachFragment")
    }

    override fun onStart()
    {
        super.onStart()
        Log.e("baseActivity", "onStart")
    }

    override fun onResume()
    {
        super.onResume()
        Log.e("baseActivity", "onResume")
    }

    override fun onPause()
    {
        super.onPause()
        Log.e("baseActivity", "onPause")
    }

    override fun onStop()
    {
        super.onStop()
        Log.e("baseActivity", "onStop")
    }

    override fun onRestart()
    {
        super.onRestart()
        Log.e("MainActivity", "onRestart")
    }

    override fun onBackPressed()
    {
        Log.e("baseActivity", "onBackPressed")
    }

    override fun onDestroy()
    {
        super.onDestroy()
        Log.e("baseActivity", "onDestroy")
    }

    fun SetMenuID(@MenuRes menuRes: Int)
    {
        this._menuRes = menuRes
    }

    fun SetMenuClickListener(onMenuItemClickListener: (MenuItem) -> Boolean)
    {
//        this._onMenuItemClickListener =
        _mToolbar!!.setOnMenuItemClickListener(onMenuItemClickListener)
    }

    fun SetMenuClickListener(onMenuItemClickListener: FragmentMenuItemClickListener)
    {
        _mToolbar!!.setOnMenuItemClickListener(onMenuItemClickListener.FragmentMenuItemClickListenerObject())
    }

    fun SetMenu(@MenuRes menuRes: Int, onMenuItemClickListener: (MenuItem) -> Boolean)
    {
        this._menuRes = menuRes
        this._onMenuItemClickListener = onMenuItemClickListener
    }

    fun SetLeftImg(@DrawableRes imgId: Int)
    {
        _mToolbar!!.setNavigationIcon(imgId)
    }

    fun SetActivityTitle(text: String)
    {
        _toolBarTitle!!.text = text
    }

    fun SetActivityTitle(@StringRes textId: Int)
    {
        _toolBarTitle!!.setText(textId)
    }

    fun SetRightText(text: String)
    {
        //amRightTv!!.text = text
    }

    fun SetRightTextAndClickListener(text: String, listener: View.OnClickListener)
    {
//        amRightTv!!.text = text
//        amRightTv!!.setOnClickListener(listener)
    }

    fun SetRightImageAndClickListener(@DrawableRes imgId: Int, listener: View.OnClickListener)
    {
        _toolbarImage!!.setImageDrawable(ContextCompat.getDrawable(this, imgId))
        _toolbarImage!!.setOnClickListener(listener)
    }

    fun SetRightImg(@DrawableRes imgId: Int)
    {
//        amRightTv!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, imgId, 0)
    }

    fun SetOnNavigationClickListener(onClickListener : (View) -> Unit)
    {
        this._onNavigationClickListener = onClickListener
    }

    fun SetDrawerListener(drawerToggle: ActionBarDrawerToggle)
    {
        drawerToggle.syncState()
        _mDrawerLayout!!.addDrawerListener(drawerToggle)
    }

    fun SetDrawMenuAdapterAndItemClickListener(adapter: SimpleAdapter,
                                               itemClickListener: (AdapterView<*>?, View?, Int, Long) -> Unit)
    {
        _drawerMenuListView!!.adapter = adapter
        _drawerMenuListView!!.setOnItemClickListener(itemClickListener)
    }

    private fun BeforeSetActionBar()
    {
        _mToolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        _mToolbar!!.setTitleTextColor(Color.WHITE)
        _mToolbar!!.title = ""
        _mToolbar!!.isEnabled = true
    }

    //abstract fun InitActionBar()

    private fun AfterSetActionBar()
    {
        setSupportActionBar(_mToolbar)

        if (supportActionBar != null)
        {
            supportActionBar!!.setDisplayShowTitleEnabled(false)

            //use DrawerLayout
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        _mToolbar!!.setNavigationOnClickListener(_onNavigationClickListener)
//        _mToolbar!!.setOnMenuItemClickListener(_onMenuItemClickListener)
    }

    abstract fun InitActionBar()

    abstract fun FindView()

    abstract fun InitView()
}