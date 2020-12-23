package com.ai.testApp

import android.app.Application
import android.content.Context

class TestApp : Application() {

    companion object {
        private val SHARED_PREF_KEY = "sharedPref"
        private val EMPTY_STRING = ""
        lateinit var appContext: Context

        fun getStringFromSharedPref(key: String): String {
            return appContext
                .getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
                .getString(key, EMPTY_STRING)!!
        }

        fun putStringToSharedPref(key: String, value: String) {
            appContext.getSharedPreferences(
                SHARED_PREF_KEY, Context.MODE_PRIVATE)
                .edit()
                .putString(key, value)
                .apply()
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}