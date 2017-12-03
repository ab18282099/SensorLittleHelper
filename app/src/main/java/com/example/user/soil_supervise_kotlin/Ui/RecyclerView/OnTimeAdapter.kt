package com.example.user.soil_supervise_kotlin.Ui.RecyclerView

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.user.soil_supervise_kotlin.R
import com.example.user.soil_supervise_kotlin.Utility.MySharedPreferences

class OnTimeAdapter constructor(context: Context, sensorQuantity : Int, sensorDataList : ArrayList<String?>) : RecyclerView.Adapter<OnTimeAdapter.ViewHolder>()
{
    private val _context = context
    private val _sensorQuantity = sensorQuantity
    private val _sensorDataList = sensorDataList
    private val _sharePref = MySharedPreferences.InitInstance(context)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var tx_on_time_title: TextView? = null
        var tx_on_time_text: TextView? = null

        init
        {
            tx_on_time_title = itemView.findViewById<TextView>(R.id.tx_on_time_title) as TextView
            tx_on_time_text = itemView.findViewById<TextView>(R.id.tx_on_time_text) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder
    {
        val converterView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_on_time, parent, false)
        return ViewHolder(converterView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int)
    {
        if (_sensorDataList.isNotEmpty())
        {
            if (position == 0)
            {
                holder!!.tx_on_time_title!!.text = _context.getString(R.string.ID)
                holder.tx_on_time_text!!.text = _sensorDataList[position]

                holder.tx_on_time_text!!.visibility = View.VISIBLE
                holder.tx_on_time_title!!.visibility = View.VISIBLE

                holder.tx_on_time_text!!.setTextColor(Color.BLACK)
                holder.tx_on_time_title!!.setTextColor(Color.BLACK)
            }
            else if (position in 1.._sensorQuantity)
            {
                val sensorVisibility = _sharePref!!.GetSensorVisibility(position - 1)

                if (sensorVisibility == View.VISIBLE)
                {
                    holder!!.tx_on_time_title!!.visibility = sensorVisibility
                    holder.tx_on_time_text!!.visibility = sensorVisibility

                    holder.tx_on_time_title!!.text = _sharePref.GetSensorName(position - 1)

                    val warnCondition = _sharePref.GetSensorCondition(position - 1).toFloat()
                    val sensorDataFloat: Float

                    if (_sensorDataList[position] == "") sensorDataFloat = (-1).toFloat()
                    else sensorDataFloat = _sensorDataList[position]!!.toFloat()

                    if (IsWarning(sensorDataFloat, warnCondition))
                    {
                        holder.tx_on_time_text!!.text = _context.getString(R.string.warn, _sensorDataList[position], String.format("%.2f", warnCondition))
                        holder.tx_on_time_text!!.setTextColor(Color.RED)
                        holder.tx_on_time_title!!.setTextColor(Color.RED)
                    }
                    else
                    {
                        holder.tx_on_time_text!!.text = _sensorDataList[position]

                        holder.tx_on_time_text!!.setTextColor(Color.BLACK)
                        holder.tx_on_time_title!!.setTextColor(Color.BLACK)
                    }
                }
                else
                {
                    holder!!.tx_on_time_title!!.visibility = sensorVisibility
                    holder.tx_on_time_text!!.visibility = sensorVisibility
                }
            }
            else
            {
                holder!!.tx_on_time_title!!.text = _context.getString(R.string.TIME)
                holder.tx_on_time_text!!.text = _sensorDataList[position]

                holder.tx_on_time_text!!.visibility = View.VISIBLE
                holder.tx_on_time_title!!.visibility = View.VISIBLE

                holder.tx_on_time_text!!.setTextColor(Color.BLACK)
                holder.tx_on_time_title!!.setTextColor(Color.BLACK)
            }
        }
        else
        {
            holder!!.tx_on_time_title!!.visibility = View.GONE
            holder.tx_on_time_text!!.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int
    {
        if (_sensorDataList.isEmpty()) return 0
        else return _sensorQuantity + 2
    }

    private fun IsWarning(sensorData: Float?, warnConditional: Float?): Boolean
    {
        val invalidFloat = (-1).toFloat()

        if (sensorData != null && warnConditional != null && sensorData != invalidFloat && sensorData < warnConditional)
            return true

        return false
    }
}