package com.example.user.soil_supervise_kotlin.fragments

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.soil_supervise_kotlin.models.AppSettingModel
import com.example.user.soil_supervise_kotlin.R
import com.example.user.soil_supervise_kotlin.ui.dialog.SetChartDialog
import kotlinx.android.synthetic.main.fragment_chart.*

class ChartFragment : BaseFragment(), FragmentBackPressedListener {
    companion object {
        fun NewInstance(): ChartFragment {
            val fragment = ChartFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _appSettingModel: AppSettingModel? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_chart, container, false)
        _appSettingModel = AppSettingModel(activity)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_chart_dialog.text = "選擇感測器"
        btn_chart_dialog.setOnClickListener {
            val setChartDialog = SetChartDialog(activity, this)
            setChartDialog.show()
            setChartDialog.setCanceledOnTouchOutside(true)
        }
    }

    override fun OnFragmentBackPressed() {
        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain)
        vpMain.currentItem = 1
    }
}