package com.example.user.soil_supervise_kotlin.Activities

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.*
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.user.soil_supervise_kotlin.R

abstract class BaseActivity : AppCompatActivity()
{
    val TAG: String = this.javaClass.simpleName
    private var _contentView: LinearLayout? = null
    private var _mToolbar: Toolbar? = null
    private var _toolBarTitle: TextView? = null
    //private var amRightTv : TextView? = null
    private var _toolbarImage: ImageView? = null
    //
    private var _onMenuItemClickListener: ((MenuItem) -> Boolean)? = null
    private var _onNavigationClickListener: View.OnClickListener? = null
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
        if (_contentView == null && R.layout.activity_base == layoutResID)
        {
            super.setContentView(R.layout.activity_base)
            _contentView = findViewById<LinearLayout>(R.id.layout_center) as LinearLayout
            _toolBarTitle = findViewById<TextView>(R.id.toolbar_title) as TextView
            //amRightTv = findViewById<TextView>(R.id.am_right_tv) as TextView
            _toolbarImage = findViewById<ImageView>(R.id._toolbarImage) as ImageView
            _contentView!!.removeAllViews()
        }
        else if (layoutResID != R.layout.activity_base)
        {
            val nullViewGroup: ViewGroup? = null
            val addView = LayoutInflater.from(this).inflate(layoutResID, nullViewGroup)
            _contentView!!.addView(addView, ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT))

            //do not change sequence
            findView()
            initView()

            //do not change sequence
            _beforeSetActionBar()
            initActionBar()
            _afterSetActionBar()
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

    private fun _beforeSetActionBar()
    {
        _mToolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        _mToolbar!!.setNavigationIcon(R.mipmap.btn_back)
        _mToolbar!!.setTitleTextColor(Color.WHITE)
        _mToolbar!!.title = ""
        _mToolbar!!.isEnabled = true
    }

    abstract fun initActionBar()

    private fun _afterSetActionBar()
    {
        setSupportActionBar(_mToolbar)
        if (supportActionBar != null)
        {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        _mToolbar!!.setNavigationOnClickListener(_onNavigationClickListener)
        _mToolbar!!.setOnMenuItemClickListener(_onMenuItemClickListener)
    }

    abstract fun findView()

    abstract fun initView()

    fun setMenuID(@MenuRes menuRes: Int)
    {
        this._menuRes = menuRes
    }

    fun setMenu(@MenuRes menuRes: Int, onMenuItemClickListener: (MenuItem) -> Boolean)
    {
        this._menuRes = menuRes
        setMenuClickListener(onMenuItemClickListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        if (_menuRes != _invalidMenu)
        {
            menuInflater.inflate(_menuRes, menu)
        }
        return true
    }

    fun setLeftImg(@DrawableRes imgId: Int)
    {
        _mToolbar!!.setNavigationIcon(imgId)
    }

    fun setActivityTitle(text: String)
    {
        _toolBarTitle!!.text = text
    }

    fun setActivityTitle(@StringRes textId: Int)
    {
        _toolBarTitle!!.setText(textId)
    }

    fun setRightText(text: String)
    {
        //amRightTv!!.text = text
    }

    fun setRightTextAndClickListener(text: String, listener: View.OnClickListener)
    {
//        amRightTv!!.text = text
//        amRightTv!!.setOnClickListener(listener)
    }

    fun setRightImageAndClickListener(@DrawableRes imgId: Int, listener: View.OnClickListener)
    {
        _toolbarImage!!.setImageDrawable(ContextCompat.getDrawable(this, imgId))
        _toolbarImage!!.setOnClickListener(listener)
    }

    fun setRightImg(@DrawableRes imgId: Int)
    {
//        amRightTv!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, imgId, 0)
    }

    fun setMenuClickListener(onMenuItemClickListener: (MenuItem) -> Boolean)
    {
        this._onMenuItemClickListener = onMenuItemClickListener
    }

    fun setOnNavigationClickListener(onNavigationClickListener: View.OnClickListener)
    {
        this._onNavigationClickListener = onNavigationClickListener
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
}