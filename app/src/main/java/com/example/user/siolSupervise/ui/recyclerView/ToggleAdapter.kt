package com.example.user.siolSupervise.ui.recyclerView

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.user.siolSupervise.R
import com.example.user.siolSupervise.models.AppSettingModel

class ToggleAdapter constructor(context: Context) : RecyclerView.Adapter<ToggleAdapter.ViewHolder>(),
        View.OnClickListener {
    private val _appSettingModel = AppSettingModel(context)
    private var onItemClickListener: RecyclerViewOnItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tx_toggle_sensor: TextView? = null
        var tx_toggle_pin: TextView? = null
        var tx_toggle_state: TextView? = null
        var tx_toggle_app: TextView? = null

        init {
            tx_toggle_sensor = itemView.findViewById<TextView>(R.id.tx_toggle_sensor) as TextView
            tx_toggle_pin = itemView.findViewById<TextView>(R.id.tx_toggle_pin) as TextView
            tx_toggle_state = itemView.findViewById<TextView>(R.id.tx_toggle_state) as TextView
            tx_toggle_app = itemView.findViewById<TextView>(R.id.tx_toggle_app) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val converterView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_pin_list, parent, false)
        converterView.setOnClickListener(this)
        return ViewHolder(converterView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tx_toggle_sensor!!.text = _appSettingModel.sensorName(position)
        holder.tx_toggle_pin!!.text = _appSettingModel.sensorPin(position)
        holder.tx_toggle_state!!.text = _appSettingModel.pinState(position)
        holder.tx_toggle_app!!.text = _appSettingModel.pinAppliance(position)

        if (_appSettingModel.sensorVisibility(position) == View.GONE) {
            holder.tx_toggle_sensor!!.setTextColor(Color.LTGRAY)
            holder.tx_toggle_pin!!.setTextColor(Color.LTGRAY)
            holder.tx_toggle_state!!.setTextColor(Color.LTGRAY)
            holder.tx_toggle_app!!.setTextColor(Color.LTGRAY)
        }
        else {
            holder.tx_toggle_sensor!!.setTextColor(Color.BLACK)
            holder.tx_toggle_pin!!.setTextColor(Color.BLACK)
            holder.tx_toggle_state!!.setTextColor(Color.BLACK)
            holder.tx_toggle_app!!.setTextColor(Color.BLACK)
        }

        holder.itemView.tag = position
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // use "CallBack"
    override fun onClick(p0: View?) {
        if (onItemClickListener != null) {
            onItemClickListener?.onRecyclerViewItemClick(p0, p0?.tag as Int)
        }
    }

    fun setOnItemClickListener(listener: RecyclerViewOnItemClickListener) {
        onItemClickListener = listener
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun getItemCount(): Int {
        return if (_appSettingModel.sensorQuantity() > 0) _appSettingModel.sensorQuantity() else 0
    }
}