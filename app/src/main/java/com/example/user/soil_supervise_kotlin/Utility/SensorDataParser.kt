package com.example.user.soil_supervise_kotlin.Utility

import android.content.Context
import android.util.Log
import com.example.user.soil_supervise_kotlin.Model.AppSettingModel
import org.json.JSONArray
import org.json.JSONException

class SensorDataParser constructor(context: Context)
{
    private val _appSettingModel = AppSettingModel(context)

    fun GetSensorData(jsonArray: JSONArray): ArrayList<Array<String?>>
    {
        val jsonList = ArrayList<Array<String?>>(_appSettingModel.SensorQuantity() + 2)

        try
        {
            val jsonArrayLength: Int = jsonArray.length()

            for (i in 0 until _appSettingModel.SensorQuantity() + 2)
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
                else if (i == _appSettingModel.SensorQuantity() + 1)
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