package com.example.user.soil_supervise_kotlin.activities

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
import com.example.user.soil_supervise_kotlin.fragments.FragmentMenuItemClickListener
import com.example.user.soil_supervise_kotlin.R

abstract class BaseActivity : AppCompatActivity()
{
    private var _contentView: LinearLayout? = null //layout_center
    private var _mDrawerLayout: DrawerLayout? = null
    private var _drawerMenuListView: ListView? = null
    private var _mToolbar: Toolbar? = null
    private var _toolBarTitle: TextView? = null
    private var _toolbarImage: ImageView? = null
    private var _onNavigationClickListener: ((View) -> Unit)? = null

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

    fun SetMenuClickListener(onMenuItemClickListener: FragmentMenuItemClickListener)
    {
        _mToolbar!!.setOnMenuItemClickListener(onMenuItemClickListener.FragmentMenuItemClickListenerObject())
    }

    fun SetActivityTitle(text: String)
    {
        _toolBarTitle!!.text = text
    }

    fun SetRightImageAndClickListener(@DrawableRes imgId: Int, listener: View.OnClickListener)
    {
        _toolbarImage!!.setImageDrawable(ContextCompat.getDrawable(this, imgId))
        _toolbarImage!!.setOnClickListener(listener)
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

    //abstract fun InitActionBar

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
    }

    abstract fun InitActionBar()

    abstract fun FindView()

    abstract fun InitView()
}