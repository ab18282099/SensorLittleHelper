package com.example.user.siolSupervise.db

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import com.example.user.siolSupervise.ui.dialog.ProgressDialog

class HttpHelper private constructor(context: Context) {
    companion object {
        @Volatile
        private var instance: HttpHelper? = null

        fun initInstance(context: Context): HttpHelper? {
            if (instance == null) {
                synchronized(HttpHelper::class.java) {
                    if (instance == null)
                        instance = HttpHelper(context)
                }
            }
            return instance
        }
    }

    private var _httpThread: HandlerThread? = null
    private var _uiHandler: Handler? = null
    private var _httpAction: IHttpAction? = null
    private val _progressDialog: AlertDialog = ProgressDialog.dialogProgress(context, "連接中…", View.VISIBLE)

    fun setHttpAction(action: IHttpAction) {
        _httpAction = action
    }

    fun startHttpThread() {
        if (_httpAction != null) {
            _progressDialog.show()
            _progressDialog.setCancelable(false)

            _httpThread = HandlerThread("HttpThread")
            _httpThread!!.start()
            _uiHandler = Handler(_httpThread!!.looper)
            _uiHandler!!.post {
                try {
                    _httpAction!!.onHttpRequest()
                }
                catch (e: Exception) {
                    _httpAction!!.onException(e)
                }
                finally {
                    _httpAction!!.onPostExecute()

                    _progressDialog.dismiss()
                }
            }
        }
        else {
            throw NullPointerException("Null action")
        }
    }

    fun recycleThread() {
        if (_uiHandler != null && _httpThread != null) {
            _uiHandler!!.removeCallbacksAndMessages(null)
            _httpThread!!.quitSafely()
            _httpThread!!.interrupt()
        }
    }
}