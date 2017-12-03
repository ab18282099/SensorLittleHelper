package com.example.user.soil_supervise_kotlin.Utility

import android.util.Log
import org.json.JSONArray
import org.json.JSONException

class SensorDataParser private constructor()
{
    companion object
    {
        @Volatile private var instance: SensorDataParser? = null
        fun InitInstance(): SensorDataParser?
        {
            if (instance == null)
            {
                synchronized(SensorDataParser::class.java) {
                    if (instance == null)
                    {
                        instance = SensorDataParser()
                    }
                }
            }
            return instance
        }
    }

    private var _sensorQuantity: Int = 5

    fun SetSensorQuantity(quantity: Int)
    {
        _sensorQuantity = quantity
    }

    fun GetSensorData(jsonArray: JSONArray): ArrayList<Array<String?>>
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

    fun GetJsonArrayLength(jsonArray: JSONArray): Int
    {
        if (jsonArray.length() != 0) return jsonArray.length()
        return 0
    }
}