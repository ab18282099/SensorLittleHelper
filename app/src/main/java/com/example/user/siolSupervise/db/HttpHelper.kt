package com.example.user.siolSupervise.db

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import com.example.user.siolSupervise.ui.dialog.ProgressDialog

class HttpHelper private constructor(context: Context) {
    companion object {

        /**
         * HttpHelper 的單例
         */
        @Volatile
        private var instance: HttpHelper? = null

        /**
         * 初始化 HttpHelper 的實例
         * @param context Ui context
         */
        fun initInstance(context: Context): HttpHelper? {
            if (this.instance == null) {
                synchronized(HttpHelper::class.java) {
                    if (this.instance == null)
                        this.instance = HttpHelper(context)
                }
            }
            return this.instance
        }

        /**
         * initInstance 後使用 useInstance 取得單例
         */
        fun useInstance(): HttpHelper? {
            if (this.instance == null) {
                throw NullPointerException("HttpHelper null")
            }

            return this.instance
        }
    }

    private var httpThread: HandlerThread? = null
    private var uiHandler: Handler? = null
    private var httpAction: IHttpAction? = null
    private val progressDialog: AlertDialog = ProgressDialog.dialogProgress(context, "連接中…", View.VISIBLE)

    fun setHttpAction(action: IHttpAction) {
        this.httpAction = action
    }

    fun startHttpThread() {
        if (this.httpAction != null) {
            this.progressDialog.show()
            this.progressDialog.setCancelable(false)

            this.httpThread = HandlerThread("HttpThread")
            this.httpThread!!.start()
            this.uiHandler = Handler(this.httpThread!!.looper)
            this.uiHandler!!.post {
                try {
                    this.httpAction!!.onHttpRequest()
                }
                catch (e: Exception) {
                    this.httpAction!!.onException(e)
                }
                finally {
                    this.httpAction!!.onPostExecute()

                    this.progressDialog.dismiss()
                }
            }
        }
        else {
            throw NullPointerException("Null action")
        }
    }

    fun recycleThread() {
        if (this.uiHandler != null && this.httpThread != null) {
            this.uiHandler!!.removeCallbacksAndMessages(null)
            this.httpThread!!.quitSafely()
            this.httpThread!!.interrupt()
        }
    }
}