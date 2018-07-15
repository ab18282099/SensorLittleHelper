package com.example.user.siolSupervise.dto

import android.content.Context
import com.example.user.siolSupervise.models.AppSettingModel

class PhpUrlDto constructor(context: Context) {
    private val _appSettingModel = AppSettingModel(context)
    private val _username = _appSettingModel.username()
    private val _password = _appSettingModel.password()
    private val _serverIp = _appSettingModel.serverIp()

    fun loadingHistoryDataById(id1: String, id2: String): String {
        return "http://$_serverIp/load_history.php?&server=$_serverIp&user=$_username&pass=$_password&id=$id1&id2=$id2"
    }

    fun deletedDataById(id1: String, id2: String): String {
        return "http://$_serverIp/deleted_json.php?&server=$_serverIp&user=$_username&pass=$_password&id=$id1&id2=$id2"
    }

    fun connectDbByLogin(username: String, password: String): String {
        return "http://$_serverIp/conn_json.php?&server=$_serverIp&user=$username&pass=$password"
    }

    fun editingSensorQuantityByQuery(query: String, sensorId: String): String {
        return "http://$_serverIp/$query.php?&server=$_serverIp&user=$_username&pass=$_password&sensor_id=$sensorId"
    }

    val LoadingWholeData = "http://$_serverIp/android_mysql.php?&server=$_serverIp&user=$_username&pass=$_password"
    val CleanDatabase = "http://$_serverIp/clean_db.php?&server=$_serverIp&user=$_username&pass=$_password"
    val LoadingLastData = "http://$_serverIp/android_mysql_last.php?&server=$_serverIp&user=$_username&pass=$_password"
}