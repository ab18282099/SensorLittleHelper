package com.example.user.soil_supervise_kotlin.MySqlDb

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import com.example.user.soil_supervise_kotlin.Ui.ProgressDialog

class HttpHelper private constructor(context: Context)
{
    companion object
    {
        @Volatile private var instance: HttpHelper? = null
        fun InitInstance(context: Context): HttpHelper?
        {
            if (instance == null)
            {
                synchronized(HttpHelper::class.java) {
                    if (instance == null)
                        instance = HttpHelper(context)
                }
            }
            return instance
        }
    }

    private var _httpThread : HandlerThread? = null
    private var _uiHandler: Handler? = null
    private var _httpAction: IHttpAction? = null
    private val _progressDialog : AlertDialog = ProgressDialog.DialogProgress(context, "連接中…", View.VISIBLE)

    fun SetHttpAction(action: IHttpAction)
    {
        _httpAction = action
    }

    fun StartHttpThread()
    {
        if (_httpAction != null)
        {
            _progressDialog.show()
            _progressDialog.setCancelable(false)

            _httpThread = HandlerThread("HttpThread")
            _httpThread!!.start()
            _uiHandler = Handler(_httpThread!!.looper)
            _uiHandler!!.post {
                try
                {
                    _httpAction!!.OnHttpRequest()
                }
                catch (e : Exception)
                {
                    _httpAction!!.OnException(e)
                }
                finally
                {
                    _httpAction!!.OnPostExecute()

                    _progressDialog.dismiss()
                }
            }
        }
        else
        {
            throw NullPointerException("Null action")
        }
    }

    fun RecycleThread()
    {
        if (_uiHandler != null && _httpThread != null)
        {
            _uiHandler!!.removeCallbacksAndMessages(null)
            _httpThread!!.quitSafely()
            _httpThread!!.interrupt()
        }
    }
}