package com.pavelvorobyev.wifideviceschecker.view

import android.content.Context

interface MainPresenter {

    val view: MainView

    fun startServer(port: String, context: Context)

}