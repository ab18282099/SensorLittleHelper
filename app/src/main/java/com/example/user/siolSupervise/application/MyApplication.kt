package com.example.user.siolSupervise.application

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Foreground.init(this)
    }
}
