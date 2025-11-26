package com.example.giflightsearch

import android.app.Application
import com.example.giflightsearch.data.AppContainer
import com.example.giflightsearch.data.AppDataContainer

class FlightSearchApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}