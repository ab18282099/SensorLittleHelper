package com.example.user.siolSupervise.fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.*
import com.example.user.siolSupervise.db.DbAction
import com.example.user.siolSupervise.db.IDbResponse
import com.example.user.siolSupervise.utility.ExitApplication
import com.example.user.siolSupervise.models.AppSettingModel
import com.example.user.siolSupervise.dto.PhpUrlDto
import com.example.user.siolSupervise.R
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class LoginFragment : BaseFragment(), FragmentBackPressedListener {
    companion object {
        fun NewInstance(): LoginFragment {
            val fragment = LoginFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _appSettingModel: AppSettingModel? = null
    private var _doubleBackToExit: Boolean? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _appSettingModel = AppSettingModel(activity)
        return inflater!!.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edit_user.text = SpannableStringBuilder("")

        if (_appSettingModel!!.IsRememberPassword())
            edit_pass.text = SpannableStringBuilder(_appSettingModel!!.Password())
        else
            edit_pass.text = SpannableStringBuilder("")

        rememberPass.text = "記住密碼"
        rememberPass.isChecked = _appSettingModel!!.IsRememberPassword()
        rememberPass.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                _appSettingModel!!.PutBoolean("IsRememberPassword", true)
            }
            else {
                _appSettingModel!!.PutBoolean("IsRememberPassword", false)
            }
        }

        btn_login.text = "登入"
        btn_login.setOnClickListener {
            TryConnectDataBase(edit_user.text.toString(), edit_pass.text.toString())
        }
    }

    override fun OnFragmentBackPressed() {
        if (_doubleBackToExit == true && _doubleBackToExit != null) {
            ExitApplication.InitInstance()?.Exit()
            return
        }

        this._doubleBackToExit = true
        toast("Press Back again to Exit")

        Handler().postDelayed({
            _doubleBackToExit = false
        }, 1000)
    }

    private fun TryConnectDataBase(username: String, password: String) {
        val loginAction = DbAction(activity)
        loginAction.SetResponse(object : IDbResponse {
            override fun OnSuccess(jsonObject: JSONObject) {
                if (jsonObject.getInt("success") == 1) {
                    toast("成功連接資料庫")
                    PutLoginInfo()
                    (activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager).currentItem = 1
                }
            }

            override fun OnException(e: Exception) {
                Log.e("Login database", e.toString())
                toast("連接失敗")
                PutLoginInfo()
            }

            override fun OnError(volleyError: VolleyError) {
                VolleyLog.e("ERROR", volleyError.toString())
                toast("CONNECT ERROR")
                PutLoginInfo()
            }
        })
        loginAction.DoDbOperate(PhpUrlDto(activity).ConnectDbByLogin(username, password))
    }

    private fun PutLoginInfo() {
        _appSettingModel!!.PutString("Username", edit_user.text.toString())
        _appSettingModel!!.PutString("Password", edit_pass.text.toString())
    }
}