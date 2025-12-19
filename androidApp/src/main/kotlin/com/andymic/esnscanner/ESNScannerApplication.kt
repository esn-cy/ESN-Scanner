package com.andymic.esnscanner

import android.app.Application
import qrgenerator.AppContext

class ESNScannerApplication : Application() {
    companion object {
        lateinit var INSTANCE: ESNScannerApplication
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        AppContext.apply { set(applicationContext) }
    }
}