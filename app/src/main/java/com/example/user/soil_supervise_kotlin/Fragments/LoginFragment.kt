package com.example.user.soil_supervise_kotlin.Fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.*
import com.example.user.soil_supervise_kotlin.Database.DbAction
import com.example.user.soil_supervise_kotlin.Database.IDbResponse
import com.example.user.soil_supervise_kotlin.Ui.FragmentBackPressedListener
import com.example.user.soil_supervise_kotlin.Utility.ExitApplication
import com.example.user.soil_supervise_kotlin.Utility.MySharedPreferences
import com.example.user.soil_supervise_kotlin.R
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class LoginFragment : BaseFragment(), FragmentBackPressedListener
{
    companion object
    {
        fun NewInstance(): LoginFragment
        {
            val fragment = LoginFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _sharePref: MySharedPreferences? = null

    private var _doubleBackToExit: Boolean? = null

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        Log.e("LoginFragment", "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Log.e("LoginFragment", "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater!!.inflate(R.layout.fragment_login, container, false)
        Log.e("LoginFragment", "onCreateView")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        Log.e("LoginFragment", "onActivityCreated")
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        Log.e("LoginFragment", "onViewCreated")

        _sharePref = MySharedPreferences.InitInstance(activity)

        edit_user.text = SpannableStringBuilder("")

        if (_sharePref!!.GetIsRememberPassword())
        {
            edit_pass.text = SpannableStringBuilder(_sharePref!!.GetPassword())
        }
        else edit_pass.text = SpannableStringBuilder("")

        rememberPass.text = "記住密碼"
        rememberPass.isChecked = _sharePref!!.GetIsRememberPassword()
        rememberPass.setOnCheckedChangeListener { _, b ->
            if (b)
            {
                _sharePref!!.PutBoolean("GetIsRememberPassword", true)
            }
            else
            {
                _sharePref!!.PutBoolean("GetIsRememberPassword", false)
            }
        }

        btn_login.text = "登入"
        btn_login.setOnClickListener {
            TryConnectDataBase(edit_user.text.toString(), edit_pass.text.toString())
        }
    }

    override fun onStart()
    {
        super.onStart()
        Log.e("LoginFragment", "onStart")
    }

    override fun onResume()
    {
        super.onResume()
        Log.e("LoginFragment", "onResume")
    }

    override fun onPause()
    {
        super.onPause()
        Log.e("LoginFragment", "onPause")
    }

    override fun onStop()
    {
        super.onStop()
        Log.e("LoginFragment", "onStop")
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        Log.e("LoginFragment", "onDestroyView")
    }

    override fun onDestroy()
    {
        super.onDestroy()
        Log.e("LoginFragment", "onDestroy")
    }

    override fun onDetach()
    {
        super.onDetach()
        Log.e("LoginFragment", "onDetach")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean)
    {
        super.setUserVisibleHint(isVisibleToUser)
        Log.e("LoginFragment", isVisibleToUser.toString())
    }

    override fun OnFragmentBackPressed()
    {
        if (_doubleBackToExit == true && _doubleBackToExit != null)
        {
            ExitApplication.InitInstance()?.Exit()
            return
        }

        this._doubleBackToExit = true
        toast("Press Back again to Exit")

        Handler().postDelayed({
            _doubleBackToExit = false
        }, 1000)
    }

    private fun TryConnectDataBase(user: String, pass: String)
    {
        val ServerIP = _sharePref!!.GetServerIP()
        val phpAddress = "http://$ServerIP/conn_json.php?&server=$ServerIP&user=$user&pass=$pass"
        val loginAction = DbAction(context)
        loginAction.SetResponse(object : IDbResponse
        {
            override fun OnSuccess(jsonObject: JSONObject)
            {
                if (jsonObject.getInt("success") == 1)
                {
                    toast("成功連接資料庫")
                    PutLoginInfo()
                    (activity.findViewById<ViewPager>(R.id._vpMain) as ViewPager).currentItem = 1
                }
            }

            override fun OnException(e: Exception)
            {
                Log.e("connJSON", e.toString())
                toast("連接失敗")
                PutLoginInfo()
            }

            override fun OnError(volleyError: VolleyError)
            {
                VolleyLog.e("ERROR", volleyError.toString())
                toast("CONNECT ERROR")
                PutLoginInfo()
            }
        })
        loginAction.DoDbOperate(phpAddress)
    }

    private fun PutLoginInfo()
    {
        _sharePref!!.PutString("GetUsername", edit_user.text.toString())
        _sharePref!!.PutString("GetPassword", edit_pass.text.toString())
    }
}