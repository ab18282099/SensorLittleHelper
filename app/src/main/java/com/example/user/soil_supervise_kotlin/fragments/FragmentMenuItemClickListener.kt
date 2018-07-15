package com.example.user.soil_supervise_kotlin.fragments

import android.view.MenuItem

interface FragmentMenuItemClickListener {
    fun FragmentMenuItemClickListenerObject(): (MenuItem) -> Boolean
}