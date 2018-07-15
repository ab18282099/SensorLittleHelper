package com.example.user.siolSupervise.db

import com.android.volley.VolleyError
import org.json.JSONObject

interface IDbResponse {
    fun OnSuccess(jsonObject: JSONObject)
    fun OnException(e: Exception)
    fun OnError(volleyError: VolleyError)
}