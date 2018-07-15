package com.example.user.siolSupervise.ui.recyclerView

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.user.siolSupervise.R
import com.example.user.siolSupervise.models.AppSettingModel
import org.jetbrains.anko.textColor

class HistoryDataAdapter constructor(context: Context, sensorDataList: ArrayList<Array<String?>>,
                                     viewCount: Int) : RecyclerView.Adapter<HistoryDataAdapter.ViewHolder>() {
    private val _appSettingModel = AppSettingModel(context)
    private val _context = context
    private val _sensorDataList = sensorDataList
    private val _viewCount = viewCount

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // id
        var tx_item_id: TextView? = null
        // default 5 sensor
        var tx_item_sensor1: TextView? = null
        var tx_item_sensor2: TextView? = null
        var tx_item_sensor3: TextView? = null
        var tx_item_sensor4: TextView? = null
        var tx_item_sensor5: TextView? = null
        // customer sensor
        var tx_item_content: LinearLayout? = null
        // time
        var tx_item_time: TextView? = null

        init {
            tx_item_id = itemView.findViewById<TextView>(R.id.tx_item_id) as TextView
            tx_item_sensor1 = itemView.findViewById<TextView>(R.id.tx_item_moisture) as TextView
            tx_item_sensor2 = itemView.findViewById<TextView>(R.id.tx_item_light) as TextView
            tx_item_sensor3 = itemView.findViewById<TextView>(R.id.tx_item_sensor3) as TextView
            tx_item_sensor4 = itemView.findViewById<TextView>(R.id.tx_item_sensor4) as TextView
            tx_item_sensor5 = itemView.findViewById<TextView>(R.id.tx_item_sensor5) as TextView
            tx_item_content = itemView.findViewById<LinearLayout>(R.id.tx_item_content) as LinearLayout
            tx_item_time = itemView.findViewById<TextView>(R.id.tx_item_time) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.e("HistoryDataFragment", "onCreateViewHolder")

        val converterView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sensor_data, parent, false)
        return ViewHolder(converterView)
    }

    override fun onBindViewHolder(holder: HistoryDataAdapter.ViewHolder, position: Int) {
        Log.e("HistoryDataFragment", "onBindViewHolder")

        holder.tx_item_sensor1!!.visibility = _appSettingModel.sensorVisibility(0)
        holder.tx_item_sensor2!!.visibility = _appSettingModel.sensorVisibility(1)
        holder.tx_item_sensor3!!.visibility = _appSettingModel.sensorVisibility(2)
        holder.tx_item_sensor4!!.visibility = _appSettingModel.sensorVisibility(3)
        holder.tx_item_sensor5!!.visibility = _appSettingModel.sensorVisibility(4)

        holder.tx_item_content?.removeAllViewsInLayout()

        if (_sensorDataList.isNotEmpty()) {
            if (_appSettingModel.sensorQuantity() > 5) {
                for (i in 0 until _appSettingModel.sensorQuantity() - 5) {
                    val txCustomer = TextView(_context)
                    val sensorData = _sensorDataList[i + 6][position]
                    setSensorText(txCustomer, sensorData, i + 5)
                    txCustomer.visibility = _appSettingModel.sensorVisibility(i + 5)

                    val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (50).toFloat(), _context.resources.displayMetrics)
                    txCustomer.layoutParams = LinearLayout.LayoutParams(height.toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)

                    holder.tx_item_content!!.addView(txCustomer)
                }
            }

            holder.tx_item_id!!.text = _sensorDataList[0][position] // id dataList[0]
            holder.tx_item_time!!.text = _sensorDataList[_appSettingModel.sensorQuantity() + 1][position] // id dataList[last]

            setSensorText(holder.tx_item_sensor1, _sensorDataList[1][position], 0)
            setSensorText(holder.tx_item_sensor2, _sensorDataList[2][position], 1)
            setSensorText(holder.tx_item_sensor3, _sensorDataList[3][position], 2)
            setSensorText(holder.tx_item_sensor4, _sensorDataList[4][position], 3)
            setSensorText(holder.tx_item_sensor5, _sensorDataList[5][position], 4)
        }
    }

    override fun getItemCount(): Int {
        if (_viewCount > 0) return _viewCount
        return 0
    }

    private fun setSensorText(textView: TextView?, sensorData: String?, position: Int) {
        if (sensorData == "") {
            textView!!.text = sensorData
        }
        else {
            val warnConditional1 = _appSettingModel.warningCondition(position).toFloat()

            if (sensorData!!.toFloat() < warnConditional1) {
                textView!!.text = sensorData
                textView.textColor = Color.RED
            }
            else {
                textView!!.text = sensorData
                textView.textColor = Color.BLACK
            }
        }
    }
}