package com.example.user.soil_supervise_kotlin.OtherClass

import android.util.Log
import org.json.JSONArray
import org.json.JSONException

class SensorDataAnalyser
{
    companion object
    {
        @Volatile private var instance: SensorDataAnalyser? = null
        fun initInstance(): SensorDataAnalyser?
        {
            if (instance == null)
            {
                synchronized(SensorDataAnalyser::class.java) {
                    if (instance == null)
                    {
                        instance = SensorDataAnalyser()
                    }
                }
            }
            return instance
        }
    }

    private var _sensorQuantity: Int = 5
    private var _sharePref: MySharedPreferences? = null

    fun setSensorQuantity(quantity: Int)
    {
        _sensorQuantity = quantity
    }

    fun setSharePref(sharePref: MySharedPreferences?)
    {
        this._sharePref = sharePref
    }

    fun getSensorData(jsonArray: JSONArray): ArrayList<Array<String?>>
    {
        val jsonList = ArrayList<Array<String?>>(_sensorQuantity + 2)

        try
        {
            val jsonArrayLength: Int = jsonArray.length()

            for (i in 0 until _sensorQuantity + 2)
            {
                if (i == 0)
                {
                    val arrayTemp = arrayOfNulls<String?>(jsonArrayLength)

                    for (j in 0 until jsonArrayLength)
                    {
                        arrayTemp[j] = jsonArray.getJSONObject(j).getString("ID")
                    }

                    jsonList.add(i, arrayTemp)
                }
                else if (i == _sensorQuantity + 1)
                {
                    val arrayTemp = arrayOfNulls<String?>(jsonArrayLength)

                    for (k in 0 until jsonArrayLength)
                    {
                        arrayTemp[k] = jsonArray.getJSONObject(k).getString("time")
                    }

                    jsonList.add(i, arrayTemp)
                }
                else
                {
                    val arrayTemp = arrayOfNulls<String?>(jsonArrayLength)

                    for (l in 0 until jsonArrayLength)
                    {
                        arrayTemp[l] = jsonArray.getJSONObject(l).getString("sensor_" + (i).toString())
                    }

                    jsonList.add(i, arrayTemp)
                }
            }
        }
        catch (e: JSONException)
        {
            Log.e("AnalysisFailed", e.toString())
        }

        return jsonList
    }

    fun getJsonArrayLength(jsonArray: JSONArray): Int
    {
        if (jsonArray.length() != 0) return jsonArray.length()
        return 0
    }
}