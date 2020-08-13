package com.okurchenko.ecocity

import android.app.Application
import com.okurchenko.ecocity.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class StationsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@StationsApplication)
            modules(appModules)
        }
    }
}
