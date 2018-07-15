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
        fun newInstance(): LoginFragment {
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

        if (_appSettingModel!!.isRememberPassword())
            edit_pass.text = SpannableStringBuilder(_appSettingModel!!.password())
        else
            edit_pass.text = SpannableStringBuilder("")

        rememberPass.text = "記住密碼"
        rememberPass.isChecked = _appSettingModel!!.isRememberPassword()
        rememberPass.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                _appSettingModel!!.putBoolean("isRememberPassword", true)
            }
            else {
                _appSettingModel!!.putBoolean("isRememberPassword", false)
            }
        }

        btn_login.text = "登入"
        btn_login.setOnClickListener {
            tryConnectDataBase(edit_user.text.toString(), edit_pass.text.toString())
        }
    }

    override fun onFragmentBackPressed() {
        if (_doubleBackToExit == true && _doubleBackToExit != null) {
            ExitApplication.initInstance()?.exit()
            return
        }

        this._doubleBackToExit = true
        toast("Press Back again to exit")

        Handler().postDelayed({
            _doubleBackToExit = false
        }, 1000)
    }

    private fun tryConnectDataBase(username: String, password: String) {
        val loginAction = DbAction(activity)
        loginAction.setResponse(object : IDbResponse {
            override fun onSuccess(jsonObject: JSONObject) {
                if (jsonObject.getInt("success") == 1) {
                    toast("成功連接資料庫")
                    putLoginInfo()
                    (activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager).currentItem = 1
                }
            }

            override fun onException(e: Exception) {
                Log.e("Login database", e.toString())
                toast("連接失敗")
                putLoginInfo()
            }

            override fun onError(volleyError: VolleyError) {
                VolleyLog.e("ERROR", volleyError.toString())
                toast("CONNECT ERROR")
                putLoginInfo()
            }
        })
        loginAction.doDbOperate(PhpUrlDto(activity).connectDbByLogin(username, password))
    }

    private fun putLoginInfo() {
        _appSettingModel!!.putString("username", edit_user.text.toString())
        _appSettingModel!!.putString("password", edit_pass.text.toString())
    }
}