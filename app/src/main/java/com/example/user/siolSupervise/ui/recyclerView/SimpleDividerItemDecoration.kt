package com.example.user.siolSupervise.ui.recyclerView

import android.content.Context
import android.graphics.Canvas
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import com.example.user.siolSupervise.R

class SimpleDividerItemDecoration constructor(context: Context) : RecyclerView.ItemDecoration() {
    private val _mDivider = ContextCompat.getDrawable(context, R.drawable.divider_line)

    override fun onDrawOver(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        val left = parent!!.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + _mDivider!!.intrinsicHeight

            _mDivider.setBounds(left, top, right, bottom)
            _mDivider.draw(c)
        }
    }
}