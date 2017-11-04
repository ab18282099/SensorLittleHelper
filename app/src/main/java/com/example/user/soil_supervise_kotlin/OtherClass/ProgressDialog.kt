package com.example.user.soil_supervise_kotlin.OtherClass

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import com.example.user.soil_supervise_kotlin.R

class ProgressDialog
{
    companion object
    {
        fun dialogProgress(context: Context, title: String, progressVisibility: Int): AlertDialog
        {
            val nullParent: ViewGroup? = null
            val convertView = LayoutInflater.from(context).inflate(R.layout.dialog_progress, nullParent)

            val progressBar = convertView.findViewById<ProgressBar>(R.id.progressBar) as ProgressBar
            val tx_connecting = convertView.findViewById<TextView>(R.id.tx_connecting) as TextView

            progressBar.visibility = progressVisibility
            tx_connecting.text = title

            val dialog = android.app.AlertDialog.Builder(context).setView(convertView).create()

            // init other fun

            return dialog
        }
    }
}