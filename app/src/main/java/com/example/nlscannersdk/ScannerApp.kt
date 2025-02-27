package com.example.nlscannersdk

import android.app.Application

class ScannerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

    }

    companion object {
        lateinit var instance: ScannerApp
            private set
    }
}