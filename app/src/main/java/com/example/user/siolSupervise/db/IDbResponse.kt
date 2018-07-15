package com.example.user.siolSupervise.db

import com.android.volley.VolleyError
import org.json.JSONObject

interface IDbResponse {
    fun onSuccess(jsonObject: JSONObject)
    fun onException(e: Exception)
    fun onError(volleyError: VolleyError)
}