package com.example.user.soil_supervise_kotlin.Model

import android.content.Context
import android.view.View

class AppSettingModel constructor(context: Context)
{
    private val _modelContext = context.getSharedPreferences("ApplicationSettingModel", Context.MODE_PRIVATE)

    fun ServerIp(): String
    {
        return _modelContext.getString("ServerIp", "192.168.43.212")// Get ServerIp
    }

    fun WifiPort(): String
    {
        return _modelContext.getString("WifiPort", "80")// Get esp8266's port
    }

    fun WifiIp(): String
    {
        return _modelContext.getString("WifiIp", "192.168.43.211")// Get  esp8266's ip
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////-Sensor Setting-/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun IsUsingSensor(position: Int): Boolean
    {
        return _modelContext.getBoolean("IsUsingSensor" + position.toString(), false)
    }

    fun SensorQuantity(): Int
    {
        return _modelContext.getInt("SensorQuantity", 5)
    }

    fun SensorName(position: Int): String
    {
        return _modelContext.getString("getSensor" + position.toString() + "Name", "def" + position.toString())
    }

    fun WarningCondition(position: Int): String
    {
        return _modelContext.getString("getSensor" + position.toString() + "Condition", "0")// Get warning condition for moisture
    }

    fun SensorVisibility(position: Int): Int
    {
        return _modelContext.getInt("getSensor" + position.toString() + "Visibility", View.GONE)
    }

    fun SensorPin(position: Int): String
    {
        return _modelContext.getString("getSensor" + position.toString() + "Pin", position.toString())
    }

    fun PinState(position: Int): String
    {
        return _modelContext.getString("getPin" + position.toString() + "State", "OFF")
    }

    fun PinAppliance(position: Int): String
    {
        return _modelContext!!.getString("getPin" + position.toString() + "App", "無")
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun FileSavedName(): String
    {
        return _modelContext.getString("FileSavedName", "default")
    }

    fun IsAutoToggle(): Boolean
    {
        return _modelContext.getBoolean("IsAutoToggle", false)
    }

    fun Username(): String
    {
        return _modelContext.getString("Username", "noUser")
    }

    fun Password(): String
    {
        return _modelContext.getString("Password", "noPass")
    }

    fun IsRememberPassword(): Boolean
    {
        return _modelContext.getBoolean("IsRememberPassword", false)
    }

    fun PutString(Index: String, Data: String?)
    {
        _modelContext.edit().putString(Index, Data).apply()
    }

    fun PutInt(Index: String, Data: Int)
    {
        _modelContext.edit().putInt(Index, Data).apply()
    }

    fun PutBoolean(Index: String, Data: Boolean)
    {
        _modelContext.edit().putBoolean(Index, Data).apply()
    }
}
