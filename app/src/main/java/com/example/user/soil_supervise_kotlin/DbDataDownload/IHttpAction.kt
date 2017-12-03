package com.example.user.soil_supervise_kotlin.DbDataDownload

interface IHttpAction
{
    fun OnHttpRequest()
    fun OnException(e : Exception)
    fun OnPostExecute()
}