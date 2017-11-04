package com.example.user.soil_supervise_kotlin.Application

import android.app.Application

class MyApplication : Application()
{
    override fun onCreate()
    {
        super.onCreate()
        Foreground.init(this)
    }
}
