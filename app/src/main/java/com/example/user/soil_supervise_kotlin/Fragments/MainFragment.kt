package com.example.user.soil_supervise_kotlin.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.SimpleAdapter
import com.example.user.soil_supervise_kotlin.OtherClass.ExitApplication
import com.example.user.soil_supervise_kotlin.R
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import java.util.ArrayList
import java.util.HashMap
import com.example.user.soil_supervise_kotlin.Interfaces.FragmentBackPressedListener
import com.example.user.soil_supervise_kotlin.OtherClass.MySharedPreferences


class MainFragment : BaseFragment(), FragmentBackPressedListener
{
    companion object
    {
        fun newInstance(): MainFragment
        {
            val fragment = MainFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private val _images = intArrayOf(R.drawable.wifi, R.drawable.current, R.drawable.historydata, R.drawable.setting, R.drawable.chart, R.drawable.exit)
    private val _imgTextList = arrayOf("Wi-Fi 遙控", "即時監控", "歷史數據", "設定", "數據折線圖", "離開程式")
    private var _gridMain: GridView? = null

    private var _sharePref: MySharedPreferences? = null

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        Log.e("MainFragment", "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.e("MainFragment", "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater!!.inflate(R.layout.fragment_main, container, false)
        _gridMain = view.findViewById(R.id._gridMain)

        _sharePref = MySharedPreferences.initInstance(activity)

        Log.e("MainFragment", "onCreateView")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        Log.e("MainFragment", "onActivityCreated")
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        Log.e("MainFragment", "onViewCreated")

        val items = ArrayList<Map<String, Any>>()
        for (i in _images.indices)
        {
            val item = HashMap<String, Any>()
            item.put("_images", _images[i])
            item.put("text", _imgTextList[i])
            items.add(item)
        }
        val adapter = SimpleAdapter(activity, items, R.layout.grid_main_item, arrayOf("_images", "text"), intArrayOf(R.id._images, R.id.image_text))
        _gridMain!!.numColumns = 2
        _gridMain!!.adapter = adapter
        _gridMain!!.setOnItemClickListener { adapterView, viewGrid, i, l ->
            // use getActivity() to get mother-activity and find view which we'll use
            val vpMain = activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager
            when (i)
            {
                0 -> vpMain.currentItem = 2
                1 -> vpMain.currentItem = 3
                2 -> vpMain.currentItem = 4
                3 -> vpMain.currentItem = 6 // setting
                4 -> vpMain.currentItem = 5 // chart
                5 ->
                {
                    alert("離開程式?") {
                        yesButton { ExitApplication.initInstance()!!.exit() }
                        noButton { }
                    }.show()
                }
            }
        }
    }

    override fun onStart()
    {
        super.onStart()
        Log.e("MainFragment", "onStart")
    }

    override fun onResume()
    {
        super.onResume()
        Log.e("MainFragment", "onResume")
    }

    override fun onPause()
    {
        super.onPause()
        Log.e("MainFragment", "onPause")
    }

    override fun onStop()
    {
        super.onStop()
        Log.e("MainFragment", "onStop")
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        Log.e("MainFragment", "onDestroyView")
    }

    override fun onDestroy()
    {
        super.onDestroy()
        Log.e("MainFragment", "onDestroy")
    }

    override fun onDetach()
    {
        super.onDetach()
        Log.e("MainFragment", "onDetach")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean)
    {
        super.setUserVisibleHint(isVisibleToUser)
        Log.e("MainFragment", isVisibleToUser.toString())
    }

    override fun OnFragmentBackPressed()
    {
        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager
        vpMain.currentItem = 0
    }
}