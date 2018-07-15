package com.example.user.siolSupervise.utility

import android.app.Activity
import java.util.LinkedList

class ExitApplication {
    companion object {
        @Volatile
        private var instance: ExitApplication? = null

        fun InitInstance(): ExitApplication? {
            if (instance == null) {
                synchronized(ExitApplication::class.java) {
                    if (instance == null) {
                        instance = ExitApplication()
                    }
                }
            }
            return instance
        }
    }

    private val _activityList = LinkedList<Activity>()

    // add open activities in list_sensor_data
    fun AddActivity(activity: Activity) {
        _activityList.add(activity)
    }

    // use "for" to close all activities
    fun Exit() {
        for (activity in _activityList) {
            activity.finish()
        }
        System.exit(0)
    }
}