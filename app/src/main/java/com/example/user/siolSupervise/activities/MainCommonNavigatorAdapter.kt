package com.example.user.siolSupervise.activities

import android.content.Context
import android.graphics.Color
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.user.siolSupervise.R
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView
import org.jetbrains.anko.textColor

/**
 * 首頁 ViewPager 底部導覽列轉接
 * @param fragmentTitleList fragment 頁面標題清單
 * @param fragmentImgList fragment 頁面圖示清單
 * @param viewPager MainActivity ViewPager
 */
class MainCommonNavigatorAdapter(
        private val fragmentTitleList: ArrayList<String>,
        private val fragmentImgList: ArrayList<Int>,
        private val viewPager: ViewPager?) : CommonNavigatorAdapter() {

    /**
     * 取得數量
     */
    override fun getCount(): Int {
        if (this.fragmentTitleList.isEmpty()) return 0

        return this.fragmentTitleList.size
    }

    /**
     * 取得 Indicator
     * @param context Ui context
     */
    override fun getIndicator(context: Context?): IPagerIndicator {
        val indicator = LinePagerIndicator(context)
        indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
        return indicator
    }

    /**
     * 取得標題畫面
     * @param context ui context
     * @param index
     */
    override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
        val commonPagerTitleView = CommonPagerTitleView(context)
        commonPagerTitleView.setContentView(R.layout.image_pager_title)

        val titleImg = commonPagerTitleView.findViewById<ImageView>(R.id.title_img)
        titleImg.setImageResource(this.fragmentImgList[index])

        val titleText = commonPagerTitleView.findViewById<TextView>(R.id.title_text)
        titleText.text = this.fragmentTitleList[index]
        titleText.visibility = View.GONE

        commonPagerTitleView.setOnClickListener {
            this.viewPager?.currentItem = index
        }

        commonPagerTitleView.onPagerTitleChangeListener = object : CommonPagerTitleView.OnPagerTitleChangeListener {
            override fun onSelected(index: Int, totalCount: Int) {
                titleText.textColor = Color.BLACK
            }

            override fun onDeselected(index: Int, totalCount: Int) {
                titleText.textColor = Color.LTGRAY
            }

            override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
                titleText.visibility = View.VISIBLE

                titleImg.scaleX = (0.8f + (1.3f - 0.8f) * enterPercent)
                titleImg.scaleY = (0.8f + (1.3f - 0.8f) * enterPercent)
            }

            override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
                titleText.visibility = View.GONE

                titleImg.scaleX = (1.3f + (0.8f - 1.3f) * leavePercent)
                titleImg.scaleY = (1.3f + (0.8f - 1.3f) * leavePercent)
            }
        }

        return commonPagerTitleView
    }
}