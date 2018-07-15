package com.example.user.siolSupervise.fragments

import android.view.MenuItem

interface FragmentMenuItemClickListener {
    fun fragmentMenuItemClickListenerObject(): (MenuItem) -> Boolean
}