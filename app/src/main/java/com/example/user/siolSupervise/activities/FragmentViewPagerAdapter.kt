package com.example.user.siolSupervise.activities

import android.app.Fragment
import android.app.FragmentManager
import android.support.v13.app.FragmentPagerAdapter

/**
 * Fragment ViewPager 轉接器
 * @param fragmentManager FragmentManager
 * @param fragmentList fragment 清單
 */
class FragmentViewPagerAdapter constructor(fragmentManager: FragmentManager,
                                           private val fragmentList: ArrayList<Fragment>) : FragmentPagerAdapter(fragmentManager) {

    /**
     * 取得 fragment 數量
     */
    override fun getCount(): Int {
        return this.fragmentList.size
    }

    /**
     * 取得 fragment list 的項目
     * @param position 顯示位置
     */
    override fun getItem(position: Int): Fragment {
        var fragment = Fragment()
        when (position) {
        // login
            0 -> fragment = this.fragmentList[0]

        // main
            1 -> fragment = this.fragmentList[1]

        // toggle
            2 -> fragment = this.fragmentList[2]

        // on_time
            3 -> fragment = this.fragmentList[3]

        // history
            4 -> fragment = this.fragmentList[4]

        // chart
            5 -> fragment = this.fragmentList[5]

        // setting
            6 -> fragment = this.fragmentList[6]
        }

        return fragment
    }

    /**
     * 由指定 position 取得當前 fragment instance
     * @param position fragment 位置
     */
    fun getFragment(position: Int): Fragment {
        return this.fragmentList[position]
    }
}
