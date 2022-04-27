package com.github.bakode.zoomintexample

import android.app.Application
import android.content.res.Resources

class App : Application() {
    companion object {
        lateinit var instance: Application
        lateinit var resourses: Resources
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        resourses = resources
    }
}