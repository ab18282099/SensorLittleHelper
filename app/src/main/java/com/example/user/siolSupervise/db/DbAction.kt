package com.example.user.siolSupervise.db

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.user.siolSupervise.ui.dialog.ProgressDialog

/**
 * 封裝使用 Volley 之 DB 操作模板
 */
class DbAction constructor(context: Context) {

    /**
     * request 序列
     */
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    /**
     * 載入畫面的彈出視窗
     */
    private val progressDialog: AlertDialog = ProgressDialog.dialogProgress(context, "連接中…", View.VISIBLE)

    /**
     * DB 回應
     */
    private var dbResponse: IDbResponse? = null

    /**
     * 設定 DB 回應介面
     * @param response Db 回應介面
     */
    fun setResponse(response: IDbResponse) {
        this.dbResponse = response
    }

    /**
     * 執行操作
     * @param phpAddress php api 網址
     */
    fun doDbOperate(phpAddress: String) {
        if (this.dbResponse != null) {
            this.progressDialog.show()
            this.progressDialog.setCancelable(false)

            val connectRequest = JsonObjectRequest(phpAddress, null, { jsonObject ->
                try {
                    this.dbResponse?.onSuccess(jsonObject)
                }
                catch (e: Exception) {
                    this.dbResponse?.onException(e)
                }
                finally {
                    this.progressDialog.dismiss()
                }
            }, { volleyError ->
                this.dbResponse?.onError(volleyError)
                this.progressDialog.dismiss()
            })

            connectRequest.retryPolicy = DefaultRetryPolicy(8000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            this.requestQueue.add(connectRequest)
        }
        else {
            throw NullPointerException("Null Response")
        }
    }
}
