package com.example.user.soil_supervise_kotlin.OtherClass

import android.app.Fragment
import android.app.FragmentManager
import android.support.v13.app.FragmentPagerAdapter

class FragmentViewPagerAdapter constructor(fragmentManager: FragmentManager,
                                           private val _fragmentList: ArrayList<Fragment>) : FragmentPagerAdapter(fragmentManager)
{
    override fun getCount(): Int
    {
        return _fragmentList.size
    }

    override fun getItem(position: Int): Fragment
    {
        var fragment = Fragment()
        when (position)
        {
            0 -> fragment = _fragmentList[0] //login
            1 -> fragment = _fragmentList[1] //main
            2 -> fragment = _fragmentList[2] //toggle
            3 -> fragment = _fragmentList[3] //on_time
            4 -> fragment = _fragmentList[4] //history
            5 -> fragment = _fragmentList[5] //chart
            6 -> fragment = _fragmentList[6] //setting
        }
        return fragment
    }

    fun GetFragment(position: Int): Fragment
    {
        return _fragmentList[position]
    }
}
