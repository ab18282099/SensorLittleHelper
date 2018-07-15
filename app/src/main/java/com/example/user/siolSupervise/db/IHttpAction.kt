package com.example.user.siolSupervise.db

interface IHttpAction {
    fun OnHttpRequest()
    fun OnException(e: Exception)
    fun OnPostExecute()
}