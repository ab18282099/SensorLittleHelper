package com.example.user.soil_supervise_kotlin.fragments

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.soil_supervise_kotlin.R

abstract class BaseFragment : Fragment() {
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.e("baseFragment", "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("baseFragment", "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_base, container, false)
        Log.e("baseFragment", "onCreateView")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e("baseFragment", "onActivityCreated")
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("baseFragment", "onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        Log.e("baseFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.e("baseFragment", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e("baseFragment", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e("baseFragment", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("baseFragment", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("baseFragment", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e("baseFragment", "onDetach")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.e("baseFragment", isVisibleToUser.toString())
    }
}