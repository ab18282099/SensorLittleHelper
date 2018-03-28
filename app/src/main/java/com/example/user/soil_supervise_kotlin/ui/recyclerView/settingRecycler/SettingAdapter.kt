package com.example.user.soil_supervise_kotlin.ui.recyclerView.settingRecycler

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import com.example.user.soil_supervise_kotlin.R
import com.example.user.soil_supervise_kotlin.models.AppSettingModel
import org.jetbrains.anko.toast

class SettingAdapter constructor(context: Context,
                                 mainSettingDataTemp : Array<String?>,
                                 mainSettingDataText : Array<String?>) : RecyclerView.Adapter<SettingAdapter.ViewHolder>()
{
    private val _context = context
    private val _appSettingModel = AppSettingModel(context)
    private val _mainSettingDataTemp = mainSettingDataTemp
    private val _mainSettingDataText = mainSettingDataText

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var tx_settingTitle: TextView? = null
        var edit_setting: EditText? = null
        var switch_setting: Switch? = null

        init
        {
            tx_settingTitle = itemView.findViewById<TextView>(R.id.tx_settingTitle) as TextView
            edit_setting = itemView.findViewById<EditText>(R.id.edit_setting) as EditText
            switch_setting = itemView.findViewById<Switch>(R.id.switch_setting) as Switch
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val converterView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_setting, parent, false)
        return ViewHolder(converterView)
    }

    override fun getItemCount(): Int
    {
        return 5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        if (holder.adapterPosition == 4)
        {
            holder.switch_setting!!.visibility = View.VISIBLE
            holder.switch_setting!!.isChecked = _appSettingModel.IsAutoToggle()
            if (_appSettingModel.IsAutoToggle())
            {
                holder.switch_setting!!.text = _context.getString(R.string.on)
            }
            else
            {
                holder.switch_setting!!.text = _context.getString(R.string.off)
            }
            holder.switch_setting!!.setOnCheckedChangeListener { compoundButton, b ->
                if (compoundButton.id == R.id.switch_setting)
                {
                    if (b)
                    {
                        _appSettingModel.PutBoolean("IsAutoToggle", true)
                        holder.switch_setting!!.text = _context.getString(R.string.on)
                        _context.toast("自動遙控開啟")
                    }
                    else
                    {
                        _appSettingModel.PutBoolean("IsAutoToggle", false)
                        holder.switch_setting!!.text = _context.getString(R.string.off)
                        _context.toast("自動遙控關閉")
                    }
                }
            }
            holder.tx_settingTitle!!.text = _mainSettingDataText[4]
            holder.edit_setting!!.visibility = View.GONE
        }
        else
        {
            holder.switch_setting!!.visibility = View.GONE
            holder.tx_settingTitle!!.text = _mainSettingDataText[holder.adapterPosition]

            val editable_setting = SpannableStringBuilder(_mainSettingDataTemp[holder.adapterPosition])
            holder.edit_setting!!.text = editable_setting

            holder.edit_setting!!.addTextChangedListener(object : TextWatcher
            {
                override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int)
                {
                }

                override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int)
                {
                }

                override fun afterTextChanged(arg0: Editable)
                {
                    //get data in _mainSettingDataTemp array
                    _mainSettingDataTemp[holder.adapterPosition] = arg0.toString()
                }
            })
        }
    }
}