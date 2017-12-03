package com.example.user.soil_supervise_kotlin.Fragments

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.user.soil_supervise_kotlin.DbDataDownload.HttpHelper
import com.example.user.soil_supervise_kotlin.DbDataDownload.IHttpAction
import com.example.user.soil_supervise_kotlin.Ui.RecyclerView.RecyclerViewOnItemClickListener
import com.example.user.soil_supervise_kotlin.Utility.HttpRequest
import com.example.user.soil_supervise_kotlin.Ui.ProgressDialog
import com.example.user.soil_supervise_kotlin.Utility.MySharedPreferences
import com.example.user.soil_supervise_kotlin.R
import kotlinx.android.synthetic.main.fragment_toggle.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.json.JSONObject

class ToggleFragment : BaseFragment(), FragmentBackPressedListener
{
    companion object
    {
        fun NewInstance(): ToggleFragment
        {
            val fragment = ToggleFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _sharePref: MySharedPreferences? = null
    private var _sensorQuantity = 5

    private var _recyclerToggle: RecyclerView? = null
    private var _initAdapter: InitRecyclerAdapter? = null
    private var _toggleAdapter: ToggleRecyclerViewAdapter? = null
    private var _wifiToggleHelper : HttpHelper? = null

    private inner class ToggleRecyclerViewAdapter : RecyclerView.Adapter<ToggleFragment.ToggleRecyclerViewAdapter.ViewHolder>(),
            View.OnClickListener
    {
        private var onItemClickListener: RecyclerViewOnItemClickListener? = null

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            var tx_toggle_sensor: TextView? = null
            var tx_toggle_pin: TextView? = null
            var tx_toggle_state: TextView? = null
            var tx_toggle_app: TextView? = null

            init
            {
                tx_toggle_sensor = itemView.findViewById<TextView>(R.id.tx_toggle_sensor) as TextView
                tx_toggle_pin = itemView.findViewById<TextView>(R.id.tx_toggle_pin) as TextView
                tx_toggle_state = itemView.findViewById<TextView>(R.id.tx_toggle_state) as TextView
                tx_toggle_app = itemView.findViewById<TextView>(R.id.tx_toggle_app) as TextView
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder
        {
            val converterView = LayoutInflater.from(parent?.context)
                    .inflate(R.layout.item_pin_list, parent, false)
            converterView.setOnClickListener(this)
            return ViewHolder(converterView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int)
        {
            holder!!.tx_toggle_sensor!!.text = _sharePref!!.GetSensorName(position)
            holder.tx_toggle_pin!!.text = _sharePref!!.GetSensorPin(position)
            holder.tx_toggle_state!!.text = _sharePref!!.GetPinState(position)
            holder.tx_toggle_app!!.text = _sharePref!!.GetPinApp(position)

            if (_sharePref!!.GetSensorVisibility(position) == View.GONE)
            {
                holder.tx_toggle_sensor!!.setTextColor(Color.LTGRAY)
                holder.tx_toggle_pin!!.setTextColor(Color.LTGRAY)
                holder.tx_toggle_state!!.setTextColor(Color.LTGRAY)
                holder.tx_toggle_app!!.setTextColor(Color.LTGRAY)
            }
            else
            {
                holder.tx_toggle_sensor!!.setTextColor(Color.BLACK)
                holder.tx_toggle_pin!!.setTextColor(Color.BLACK)
                holder.tx_toggle_state!!.setTextColor(Color.BLACK)
                holder.tx_toggle_app!!.setTextColor(Color.BLACK)
            }

            holder.itemView.tag = position
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // use "CallBack"
        override fun onClick(p0: View?)
        {
            if (onItemClickListener != null)
            {
                onItemClickListener?.OnRecyclerViewItemClick(p0, p0?.tag as Int)
            }
        }

        fun SetOnItemClickListener(listener: RecyclerViewOnItemClickListener)
        {
            onItemClickListener = listener
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        override fun getItemCount(): Int
        {
            if (_sensorQuantity > 0) return _sensorQuantity
            return 0
        }
    }

    private inner class InitRecyclerAdapter : RecyclerView.Adapter<ToggleFragment.InitRecyclerAdapter.ViewHolder>()
    {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            private var tx_toggle_sensor: TextView? = null
            private var tx_toggle_pin: TextView? = null

            init
            {
                tx_toggle_sensor = itemView.findViewById<TextView>(R.id.tx_toggle_sensor) as TextView
                tx_toggle_pin = itemView.findViewById<TextView>(R.id.tx_toggle_pin) as TextView
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder
        {
            val converterView = LayoutInflater.from(parent?.context)
                    .inflate(R.layout.item_pin_list, parent, false)
            return ViewHolder(converterView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int)
        {

        }

        override fun getItemCount(): Int
        {
            return 0
        }
    }

    private inner class SimpleDividerItemDecoration constructor(context: Context) : RecyclerView.ItemDecoration()
    {
        private val mDivider = ContextCompat.getDrawable(context, R.drawable.divider_line)

        override fun onDrawOver(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?)
        {
            val left = parent!!.paddingLeft
            val right = parent.width - parent.paddingRight

            val childCount = parent.childCount
            for (i in 0 until childCount)
            {
                val child = parent.getChildAt(i)

                val params = child.layoutParams as RecyclerView.LayoutParams

                val top = child.bottom + params.bottomMargin
                val bottom = top + mDivider!!.intrinsicHeight

                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }
    }

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        Log.e("ToggleFragment", "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.e("ToggleFragment", "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater!!.inflate(R.layout.fragment_toggle, container, false)
        Log.e("ToggleFragment", "onCreateView")

        _recyclerToggle = view.findViewById<RecyclerView>(R.id._recyclerToggle) as RecyclerView

        val layoutManger = LinearLayoutManager(this.context)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerToggle?.layoutManager = layoutManger

        _initAdapter = InitRecyclerAdapter()
        _recyclerToggle?.adapter = _initAdapter

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        Log.e("ToggleFragment", "onActivityCreated")
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        Log.e("ToggleFragment", "onViewCreated")

        _sharePref = MySharedPreferences.InitInstance(activity)

        tx_ip.text = getString(R.string.wifi_ip, _sharePref!!.GetIPAddress())
        tx_port.text = getString(R.string.wifi_port, _sharePref!!.GetPort())
        tx_name.text = getString(R.string.pin_title)
        tx_pin_num.text = getString(R.string.pin_num)
        tx_pin_state.text = getString(R.string.pin_state)
        tx_pin_app.text = getString(R.string.pin_app)

        btn_pin_state.text = getString(R.string.getPinState)
        btn_pin_state.setOnClickListener {
            val ipAddress = _sharePref!!.GetIPAddress()
            val port = _sharePref!!.GetPort()
            val parameterValue = "78%78"

            TryTogglePin(ipAddress, port, parameterValue)
        }
    }

    override fun onStart()
    {
        super.onStart()
        Log.e("ToggleFragment", "onStart")
    }

    override fun onResume()
    {
        super.onResume()
        Log.e("ToggleFragment", "onResume")
    }

    override fun onPause()
    {
        super.onPause()
        Log.e("ToggleFragment", "onPause")
    }

    override fun onStop()
    {
        super.onStop()
        Log.e("ToggleFragment", "onStop")
    }

    override fun onDestroyView()
    {
        super.onDestroyView()

        Log.e("ToggleFragment", "onDestroyView")
    }

    override fun onDestroy()
    {
        super.onDestroy()
        Log.e("ToggleFragment", "onDestroy")
    }

    override fun onDetach()
    {
        super.onDetach()
        Log.e("ToggleFragment", "onDetach")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean)
    {
        super.setUserVisibleHint(isVisibleToUser)
        Log.e("ToggleFragment", isVisibleToUser.toString())
    }

    override fun OnFragmentBackPressed()
    {
        val vpMain = activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager
        vpMain.currentItem = 1
    }

    fun SetSensorQuantity(quantity: Int)
    {
        _sensorQuantity = quantity
    }

    fun SetToggleRecycler()
    {
        val layoutManger = LinearLayoutManager(this.context)
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        _recyclerToggle?.layoutManager = layoutManger

        _toggleAdapter = ToggleRecyclerViewAdapter()
        _toggleAdapter?.SetOnItemClickListener(object : RecyclerViewOnItemClickListener
        {
            override fun OnRecyclerViewItemClick(view: View?, position: Int)
            {
                if (_sharePref!!.GetSensorVisibility(position) == View.GONE)
                {
                    toast("THIS SENSOR NOT IN SERVICE")
                }
                else
                {
                    val ipAddress = _sharePref!!.GetIPAddress()
                    val port = _sharePref!!.GetPort()
                    val parameterValue = _sharePref!!.GetSensorPin(position) // pin 7~12

                    TryTogglePin(ipAddress, port, parameterValue)
                }
            }
        })

        _recyclerToggle?.adapter = _toggleAdapter
        _recyclerToggle?.addItemDecoration(SimpleDividerItemDecoration(context))
    }

    private fun TryTogglePin(ipAddress: String, port: String, parameterValue: String)
    {
        _wifiToggleHelper = HttpHelper.InitInstance(context)
        _wifiToggleHelper!!.SetHttpAction(object : IHttpAction
        {
            override fun OnHttpRequest()
            {
                val requestReply = HttpRequest.SendToggleRequest(parameterValue, ipAddress, port, "pin")
                val resultDialog = ProgressDialog.DialogProgress(activity, requestReply, View.GONE)
                resultDialog.show()

                val jsonResult = JSONObject(requestReply)

                for (i in 0 until _sensorQuantity)
                {
                    _sharePref!!.PutString("getPin" + i.toString() + "State", jsonResult.getString("PIN" + _sharePref!!.GetSensorPin(i)))
                }

                runOnUiThread { _toggleAdapter!!.notifyDataSetChanged() }
            }

            override fun OnException(e: Exception)
            {
                Log.e("toggling", e.toString())
            }

            override fun OnPostExecute()
            {
            }
        })
        _wifiToggleHelper!!.StartHttpThread()
    }
}