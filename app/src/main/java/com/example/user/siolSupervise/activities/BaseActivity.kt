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

abstract class BaseActivity : AppCompatActivity() {
    private var _contentView: LinearLayout? = null //layout_center
    private var _mDrawerLayout: DrawerLayout? = null
    private var _drawerMenuListView: ListView? = null
    private var _mToolbar: Toolbar? = null
    private var _toolBarTitle: TextView? = null
    private var _toolbarImage: ImageView? = null
    private var _onNavigationClickListener: ((View) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        Log.e("baseActivity", "onCreate")
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        if (_contentView == null && R.layout.activity_base == layoutResID) // for base activity and drawerLayout
        {
            super.setContentView(R.layout.activity_base)

            _contentView = findViewById(R.id.layout_center)
            _mDrawerLayout = findViewById(R.id.drawerLayout)
            _drawerMenuListView = findViewById(R.id.listDrawerMenu)
            _toolBarTitle = findViewById(R.id.toolbar_title)
            _toolbarImage = findViewById(R.id._toolbarImage)

            _contentView!!.removeAllViews()
        }
        else if (layoutResID != R.layout.activity_base) // for other activity
        {
            val nullViewGroup: ViewGroup? = null
            val addView = LayoutInflater.from(this).inflate(layoutResID, nullViewGroup)
            _contentView!!.addView(addView, ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT))

            //do not change sequence
            findView()
            initView()

            //do not change sequence
            beforeSetActionBar()
            initActionBar()
            afterSetActionBar()
        }
        Log.e("baseActivity", "setContentView")
    }

    fun setMenuClickListener(onMenuItemClickListener: FragmentMenuItemClickListener) {
        _mToolbar!!.setOnMenuItemClickListener(onMenuItemClickListener.fragmentMenuItemClickListenerObject())
    }

    fun setActivityTitle(text: String) {
        _toolBarTitle!!.text = text
    }

    fun setRightImageAndClickListener(@DrawableRes imgId: Int, listener: View.OnClickListener) {
        _toolbarImage!!.setImageDrawable(ContextCompat.getDrawable(this, imgId))
        _toolbarImage!!.setOnClickListener(listener)
    }

    fun setOnNavigationClickListener(onClickListener: (View) -> Unit) {
        this._onNavigationClickListener = onClickListener
    }

    fun setDrawerListener(drawerToggle: ActionBarDrawerToggle) {
        drawerToggle.syncState()
        _mDrawerLayout!!.addDrawerListener(drawerToggle)
    }

    fun setDrawMenuAdapterAndItemClickListener(adapter: SimpleAdapter,
                                               itemClickListener: (AdapterView<*>?, View?, Int, Long) -> Unit) {
        _drawerMenuListView!!.adapter = adapter
        _drawerMenuListView!!.setOnItemClickListener(itemClickListener)
    }

    private fun beforeSetActionBar() {
        _mToolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        _mToolbar!!.setTitleTextColor(Color.WHITE)
        _mToolbar!!.title = ""
        _mToolbar!!.isEnabled = true
    }

    //abstract fun initActionBar

    private fun afterSetActionBar() {
        setSupportActionBar(_mToolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)

            //use DrawerLayout
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        _mToolbar!!.setNavigationOnClickListener(_onNavigationClickListener)
    }

    abstract fun initActionBar()

    abstract fun findView()

    abstract fun initView()
}