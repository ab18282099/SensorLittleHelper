package com.example.user.soil_supervise_kotlin.db

interface IHttpAction {
    fun OnHttpRequest()
    fun OnException(e: Exception)
    fun OnPostExecute()
}