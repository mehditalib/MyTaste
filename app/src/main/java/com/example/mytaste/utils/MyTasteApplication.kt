package com.example.mytaste.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class MyTasteApplication : Application(){
    private var sContext: Context? = null

    override fun onCreate() {
        super.onCreate()

        sContext = applicationContext
    }

    fun getContext(): Context? {
        return sContext
    }

}