package com.example.user.soil_supervise_kotlin.OtherClass

import android.content.Context
import android.content.SharedPreferences
import android.view.View

class MySharedPreferences constructor(context: Context)
{
    companion object
    {
        @Volatile private var instance: MySharedPreferences? = null
        fun InitInstance(context: Context): MySharedPreferences?
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
    // init SharedPreferences Change _listener
    private var _listener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    init
    {
        val PREF_NAME = "data"
        _sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun GetServerIP(): String
    {
        return _sharedPreferences!!.getString("GetServerIP", "192.168.43.212")// Get GetServerIP
    }

    fun GetPort(): String
    {
        return _sharedPreferences!!.getString("GetPort", "80")// Get esp8266's port
    }

    fun GetIPAddress(): String
    {
        return _sharedPreferences!!.getString("GetIPAddress", "192.168.43.211")// Get  esp8266's ip
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////-Sensor Setting-/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun GetCheck(position: Int): Boolean
    {
        return _sharedPreferences!!.getBoolean("GetCheck" + position.toString(), false)
    }

    fun GetSensorQuantity(): Int
    {
        return _sharedPreferences!!.getInt("GetSensorQuantity", 5)
    }

    fun GetSensorName(position: Int): String
    {
        return _sharedPreferences!!.getString("getSensor" + position.toString() + "Name", "def" + position.toString())
    }

    fun GetSensorCondition(position: Int): String
    {
        return _sharedPreferences!!.getString("getSensor" + position.toString() + "Condition", "0")// Get warning condition for moisture
    }

    fun GetSensorVisibility(position: Int): Int
    {
        return _sharedPreferences!!.getInt("getSensor" + position.toString() + "Visibility", View.GONE)
    }

    fun GetSensorPin(position: Int): String
    {
        return _sharedPreferences!!.getString("getSensor" + position.toString() + "Pin", position.toString())
    }

    fun GetPinState(position: Int): String
    {
        return _sharedPreferences!!.getString("getPin" + position.toString() + "State", "OFF")
    }

    fun GetPinApp(position: Int): String
    {
        return _sharedPreferences!!.getString("getPin" + position.toString() + "App", "無")
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun GetFileSavedName(): String
    {
        return _sharedPreferences!!.getString("GetFileSavedName", "default")
    }

    fun GetIsAutoToggle(): Boolean
    {
        return _sharedPreferences!!.getBoolean("GetIsAutoToggle", false)
    }

    fun GetUsername(): String
    {
        return _sharedPreferences!!.getString("GetUsername", "noUser")
    }

    fun GetPassword(): String
    {
        return _sharedPreferences!!.getString("GetPassword", "noPass")
    }

    fun GetIsRememberPassword(): Boolean
    {
        return _sharedPreferences!!.getBoolean("GetIsRememberPassword", false)
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

    fun GetInitListenerSetting()
    {
        _listener = SensorSettingListener()
    }

    fun GetInitListenerPinList()
    {
        _listener = PinListListener()
    }

    // method for register SharedPreferences Change FragmentMenuItemClickListenerObject
    fun RegisterPreferenceChangeListener()
    {
        _sharedPreferences!!.registerOnSharedPreferenceChangeListener(_listener)
    }

    // method for unregister SharedPreferences Change FragmentMenuItemClickListenerObject
    fun UnregisterPreferenceChangeListener()
    {
        _sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(_listener)
    }

    private fun SensorSettingListener(): SharedPreferences.OnSharedPreferenceChangeListener
    {
        return SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            when (key)
            {

            }
        }
    }

    private fun PinListListener(): SharedPreferences.OnSharedPreferenceChangeListener
    {
        return SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            when (key)
            {

            }
        }
    }
}
