package com.example.user.siolSupervise.db

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.user.siolSupervise.ui.dialog.ProgressDialog

class DbAction constructor(context: Context) {
    private val _queue: RequestQueue = Volley.newRequestQueue(context)
    private val _progressDialog: AlertDialog = ProgressDialog.dialogProgress(context, "連接中…", View.VISIBLE)
    private var _dbResponse: IDbResponse? = null

    fun setResponse(response: IDbResponse) {
        _dbResponse = response
    }

    fun doDbOperate(phpAddress: String) {
        if (_dbResponse != null) {
            _progressDialog.show()
            _progressDialog.setCancelable(false)

            val connectRequest = JsonObjectRequest(phpAddress, null, { jsonObject ->
                try {
                    _dbResponse?.onSuccess(jsonObject)
                }
                catch (e: Exception) {
                    _dbResponse?.onException(e)
                }
                finally {
                    _progressDialog.dismiss()
                }
            }, { volleyError ->
                _dbResponse?.onError(volleyError)
                _progressDialog.dismiss()
            })

            connectRequest.retryPolicy = DefaultRetryPolicy(8000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            _queue.add(connectRequest)
        }
        else {
            throw NullPointerException("Null Response")
        }
    }
}
