package com.example.user.soil_supervise_kotlin.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.android.volley.VolleyError
import com.android.volley.VolleyLog
import com.example.user.soil_supervise_kotlin.R
import com.example.user.soil_supervise_kotlin.activities.MainActivity
import com.example.user.soil_supervise_kotlin.db.DbAction
import com.example.user.soil_supervise_kotlin.db.IDbResponse
import com.example.user.soil_supervise_kotlin.dto.PhpUrlDto
import com.example.user.soil_supervise_kotlin.fragments.HistoryDataFragment
import org.jetbrains.anko.*
import org.json.JSONObject

class DeleteDataDialog constructor(context: Context, parentFragment: HistoryDataFragment) : AlertDialog(context) {
    private val _context = context
    private val _parentFragment = parentFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_deleted)
        val edit_from = findViewById<EditText>(R.id.edit_from)
        val edit_to = findViewById<EditText>(R.id.edit_to)
        val btn_clean = findViewById<Button>(R.id.btn_clean)
        val btn_confirm = findViewById<Button>(R.id.btn_confirm)

        btn_clean.text = _context.getString(R.string.clean_db)
        btn_clean.setOnClickListener {
            _context.alert("你要確定喔?") {
                yesButton {
                    TryEditDataBase(PhpUrlDto(_context).CleanDatabase)
                }
                noButton { }
            }.show()
        }

        btn_confirm.text = _context.getString(R.string.deleted)
        btn_confirm.setOnClickListener {
            val id1 = edit_from.text.toString()
            val id2 = edit_to.text.toString()
            val regIdDeleted = Regex("[1-9]\\d*")

            if (id1.matches(regIdDeleted) && id2.matches(regIdDeleted)) {
                _context.alert("你要確定喔?") {
                    yesButton {
                        TryEditDataBase(PhpUrlDto(_context).DeletedDataById(id1, id2))
                    }
                    noButton { }
                }.show()
            }
            else {
                val v = _context.vibrator
                v.vibrate(500)
                _context.toast("輸入有誤")
            }
        }
    }

    private fun TryEditDataBase(phpAddress: String) {
        val loginAction = DbAction(_context)
        loginAction.SetResponse(object : IDbResponse {
            override fun OnSuccess(jsonObject: JSONObject) {
                when (jsonObject.getString("message")) {
                    "Deleted Successfully." -> {
                        (_context as MainActivity).LoadHistoryData(_parentFragment, _context, "1", "100")
                        _context.toast("刪除成功")
                    }
                    "DB is clean." -> {
                        (_context as MainActivity).LoadHistoryData(_parentFragment, _context, "1", "100")
                        _context.toast("清空完成")
                    }
                    else -> {
                        _context.toast("操作失敗")
                    }
                }
            }

            override fun OnException(e: Exception) {
                Log.e("editSensor", e.toString())
                _context.toast(e.toString())
            }

            override fun OnError(volleyError: VolleyError) {
                VolleyLog.e("ERROR", volleyError.toString())
                _context.toast("CONNECT ERROR")
            }
        })
        loginAction.DoDbOperate(phpAddress)
    }
}