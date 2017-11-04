package com.example.user.soil_supervise_kotlin.OtherClass

import android.app.Activity
import java.util.LinkedList

class ExitApplication
{

    private val _activityList = LinkedList<Activity>()

    // add open activities in list_sensor_data
    fun addActivity(activity: Activity)
    {
        _activityList.add(activity)
    }

    // use "for" to close all activities
    fun exit()
    {
        for (activity in _activityList)
        {
            activity.finish()
        }
        System.exit(0)
    }

    companion object
    {
        @Volatile private var instance: ExitApplication? = null
        fun initInstance(): ExitApplication?
        {
            if (instance == null)
            {
                synchronized(ExitApplication::class.java) {
                    if (instance == null)
                    {
                        instance = ExitApplication()
                    }
                }
            }
            return instance
        }
    }

}