package com.example.user.soil_supervise_kotlin.ui.RecyclerView.SettingRecycler

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import com.example.user.soil_supervise_kotlin.R
import com.example.user.soil_supervise_kotlin.models.AppSettingModel

class SensorDialogAdapter constructor(context: Context) : RecyclerView.Adapter<SensorDialogAdapter.ViewHolder>()
{
    private val _appSettingModel = AppSettingModel(context)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var checkText: CheckBox? = null
        var editName: EditText? = null
        var editCondition: EditText? = null
        var editPin: EditText? = null
        var editApp: EditText? = null

        init
        {
            checkText = itemView.findViewById<CheckBox>(R.id.checkText) as CheckBox
            editName = itemView.findViewById<EditText>(R.id.editName) as EditText
            editCondition = itemView.findViewById<EditText>(R.id.editCondition) as EditText
            editPin = itemView.findViewById<EditText>(R.id.editPin) as EditText
            editApp = itemView.findViewById<EditText>(R.id.editApp) as EditText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SensorDialogAdapter.ViewHolder
    {
        val converterView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_sensor_setting, parent, false)
        return ViewHolder(converterView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int)
    {
        holder!!.checkText?.setOnCheckedChangeListener { _, b ->
            if (b)
            {
                _appSettingModel.PutBoolean("IsUsingSensor" + holder.adapterPosition.toString(), true)
                _appSettingModel.PutInt("getSensor" + holder.adapterPosition.toString() + "Visibility", View.VISIBLE)
            }
            else
            {
                _appSettingModel.PutBoolean("IsUsingSensor" + holder.adapterPosition.toString(), false)
                _appSettingModel.PutInt("getSensor" + holder.adapterPosition.toString() + "Visibility", View.GONE)
            }
        }
        holder.checkText?.isChecked = _appSettingModel.IsUsingSensor(holder.adapterPosition)

        holder.editName?.text = SpannableStringBuilder(_appSettingModel.SensorName(holder.adapterPosition))
        holder.editName?.addTextChangedListener(object : TextWatcher
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
                _appSettingModel.PutString("getSensor" + holder.adapterPosition.toString() + "Name", arg0.toString())
            }
        })

        holder.editCondition?.text = SpannableStringBuilder(_appSettingModel.WarningCondition(holder.adapterPosition))
        holder.editCondition?.addTextChangedListener(object : TextWatcher
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
                _appSettingModel.PutString("getSensor" + holder.adapterPosition.toString() + "Condition", arg0.toString())
            }
        })

        holder.editPin?.text = SpannableStringBuilder(_appSettingModel.SensorPin(holder.adapterPosition))
        holder.editPin?.addTextChangedListener(object : TextWatcher
        {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
            {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
            {
            }

            override fun afterTextChanged(p0: Editable?)
            {
                _appSettingModel.PutString("getSensor" + holder.adapterPosition.toString() + "Pin", p0.toString())
            }
        })

        holder.editApp?.text = SpannableStringBuilder(_appSettingModel.PinAppliance(holder.adapterPosition))
        holder.editApp?.addTextChangedListener(object : TextWatcher
        {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
            {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
            {
            }

            override fun afterTextChanged(p0: Editable?)
            {
                _appSettingModel.PutString("getPin" + holder.adapterPosition.toString() + "App", p0?.toString())
            }
        })
    }

    override fun getItemCount(): Int
    {
        return _appSettingModel.SensorQuantity()
    }
}