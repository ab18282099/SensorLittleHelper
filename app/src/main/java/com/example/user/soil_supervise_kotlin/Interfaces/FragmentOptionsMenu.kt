package com.example.user.soil_supervise_kotlin.Interfaces

import android.view.MenuItem

interface FragmentOptionsMenu
{
    fun FragmentMenuResource() : Int
    fun FragmentMenuItemClickListener() : (MenuItem) -> Boolean
}