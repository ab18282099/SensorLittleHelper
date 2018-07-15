package com.example.user.siolSupervise.models

import android.content.Context
import android.view.View

class AppSettingModel constructor(context: Context) {
    private val _modelContext = context.getSharedPreferences("ApplicationSettingModel", Context.MODE_PRIVATE)

    fun serverIp(): String {
        return _modelContext.getString("serverIp", "192.168.43.212")// Get serverIp
    }

    fun wifiPort(): String {
        return _modelContext.getString("wifiPort", "80")// Get esp8266's port
    }

    fun wifiIp(): String {
        return _modelContext.getString("wifiIp", "192.168.43.211")// Get  esp8266's ip
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////-Sensor Setting-/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun isUsingSensor(position: Int): Boolean {
        return _modelContext.getBoolean("isUsingSensor" + position.toString(), false)
    }

    fun sensorQuantity(): Int {
        return _modelContext.getInt("sensorQuantity", 5)
    }

    fun sensorName(position: Int): String {
        return _modelContext.getString("getSensor" + position.toString() + "Name", "def" + position.toString())
    }

    fun warningCondition(position: Int): String {
        return _modelContext.getString("getSensor" + position.toString() + "Condition", "0")// Get warning condition for moisture
    }

    fun sensorVisibility(position: Int): Int {
        return _modelContext.getInt("getSensor" + position.toString() + "Visibility", View.GONE)
    }

    fun sensorPin(position: Int): String {
        return _modelContext.getString("getSensor" + position.toString() + "Pin", position.toString())
    }

    fun pinState(position: Int): String {
        return _modelContext.getString("getPin" + position.toString() + "State", "OFF")
    }

    fun pinAppliance(position: Int): String {
        return _modelContext!!.getString("getPin" + position.toString() + "App", "無")
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun fileSavedName(): String {
        return _modelContext.getString("fileSavedName", "default")
    }

    fun isAutoToggle(): Boolean {
        return _modelContext.getBoolean("isAutoToggle", false)
    }

    fun username(): String {
        return _modelContext.getString("username", "noUser")
    }

    fun password(): String {
        return _modelContext.getString("password", "noPass")
    }

    fun isRememberPassword(): Boolean {
        return _modelContext.getBoolean("isRememberPassword", false)
    }

    fun putString(Index: String, Data: String?) {
        _modelContext.edit().putString(Index, Data).apply()
    }

    fun putInt(Index: String, Data: Int) {
        _modelContext.edit().putInt(Index, Data).apply()
    }

    fun putBoolean(Index: String, Data: Boolean) {
        _modelContext.edit().putBoolean(Index, Data).apply()
    }
}
