package com.example.user.soil_supervise_kotlin.OtherClass

import android.app.Fragment
import android.app.FragmentManager
import android.support.v13.app.FragmentPagerAdapter

class FragmentViewPagerAdapter constructor(fragmentManager: FragmentManager,
                                           private val fragmentList: ArrayList<Fragment>) : FragmentPagerAdapter(fragmentManager)
{

    override fun getCount(): Int
    {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment
    {
        var fragment = Fragment()
        when (position)
        {
            0 -> fragment = fragmentList[0] //login
            1 -> fragment = fragmentList[1] //main
            2 -> fragment = fragmentList[2] //toggle
            3 -> fragment = fragmentList[3] //on_time
            4 -> fragment = fragmentList[4] //history
            5 -> fragment = fragmentList[5] //chart
            6 -> fragment = fragmentList[6] //setting
        }
        return fragment
    }

    fun getFragment(position: Int): Fragment
    {
        return fragmentList[position]
    }

}
