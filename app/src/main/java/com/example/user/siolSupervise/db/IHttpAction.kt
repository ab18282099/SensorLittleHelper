package com.example.user.siolSupervise.db

interface IHttpAction {
    fun onHttpRequest()
    fun onException(e: Exception)
    fun onPostExecute()
}