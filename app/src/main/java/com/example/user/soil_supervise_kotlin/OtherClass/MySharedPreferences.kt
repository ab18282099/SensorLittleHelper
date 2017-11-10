package com.example.user.soil_supervise_kotlin.OtherClass

import android.content.Context
import android.content.SharedPreferences
import android.view.View

class MySharedPreferences constructor(context: Context)
{
    companion object
    {
        @Volatile private var instance: MySharedPreferences? = null
        fun initInstance(context: Context): MySharedPreferences?
        {
            if (instance == null)
            {
                synchronized(MySharedPreferences::class.java) {
                    if (instance == null)
                    {
                        instance = MySharedPreferences(context)
                    }
                }
            }
            return instance
        }
    }

    // Init ShardPreferences when activity onCreate or method called(like call dialog in DialogVolley)
    private var _sharedPreferences: SharedPreferences? = null

    init
    {
        val PREF_NAME = "data"
        _sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getServerIP(): String
    {
        return _sharedPreferences!!.getString("getServerIP", "192.168.43.212")// Get getServerIP
    }

    fun getPort(): String
    {
        return _sharedPreferences!!.getString("getPort", "80")// Get esp8266's port
    }

    fun getIPAddress(): String
    {
        return _sharedPreferences!!.getString("getIPAddress", "192.168.43.211")// Get  esp8266's ip
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////-Sensor Setting-/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun getCheck(position: Int): Boolean
    {
        return _sharedPreferences!!.getBoolean("getCheck" + position.toString(), false)
    }

    fun getSensorQuantity(): Int
    {
        return _sharedPreferences!!.getInt("getSensorQuantity", 5)
    }

    fun getSensorName(position: Int): String
    {
        return _sharedPreferences!!.getString("getSensor" + position.toString() + "Name", "def" + position.toString())
    }

    fun getSensorCondition(position: Int): String
    {
        return _sharedPreferences!!.getString("getSensor" + position.toString() + "Condition", "0")// Get warning condition for moisture
    }

    fun getSensorVisibility(position: Int): Int
    {
        return _sharedPreferences!!.getInt("getSensor" + position.toString() + "Visibility", View.GONE)
    }

    fun getSensorPin(position: Int): String
    {
        return _sharedPreferences!!.getString("getSensor" + position.toString() + "Pin", position.toString())
    }

    fun getPinState(position: Int): String
    {
        return _sharedPreferences!!.getString("getPin" + position.toString() + "State", "OFF")
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getFileSavedName(): String
    {
        return _sharedPreferences!!.getString("getFileSavedName", "default")
    }

    fun getIsAutoToggle(): Boolean
    {
        return _sharedPreferences!!.getBoolean("getIsAutoToggle", false)
    }

    fun getUser(): String
    {
        return _sharedPreferences!!.getString("getUser", "noUser")
    }

    fun getPass(): String
    {
        return _sharedPreferences!!.getString("getPass", "noPass")
    }

    fun getIsRememberPass(): Boolean
    {
        return _sharedPreferences!!.getBoolean("getIsRememberPass", false)
    }

    fun PutString(Index: String, Data: String?)
    {
        _sharedPreferences!!.edit().putString(Index, Data).apply()// method for put String
    }

    fun PutInt(Index: String, Data: Int)
    {
        _sharedPreferences!!.edit().putInt(Index, Data).apply()// method for putInt
    }

    fun PutBoolean(Index: String, Data: Boolean)
    {
        _sharedPreferences!!.edit().putBoolean(Index, Data).apply()// method for putBoolean
    }

    // init SharedPreferences Change listener
    private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    fun getInitListenerSetting()
    {
        listener = sensorSettingListener()
    }

    private fun sensorSettingListener(): SharedPreferences.OnSharedPreferenceChangeListener
    {
        return SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            when (key)
            {

            }
        }
    }

    fun getInitListenerPinList()
    {
        listener = pinListListener()
    }

    private fun pinListListener(): SharedPreferences.OnSharedPreferenceChangeListener
    {
        return SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            when (key)
            {

            }
        }
    }

    // method for register SharedPreferences Change Listener
    fun registerPreferenceChangeListener()
    {
        _sharedPreferences!!.registerOnSharedPreferenceChangeListener(listener)
    }

    // method for unregister SharedPreferences Change Listener
    fun unregisterPreferenceChangeListener()
    {
        _sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
