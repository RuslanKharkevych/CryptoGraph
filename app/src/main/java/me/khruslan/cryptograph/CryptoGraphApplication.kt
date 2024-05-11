package me.khruslan.cryptograph

import android.app.Application
import me.khruslan.cryptograph.data.dataModule
import me.khruslan.cryptograph.ui.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CryptoGraphApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(level = if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE)
            androidContext(this@CryptoGraphApplication)
            modules(dataModule, uiModule)
        }
    }
}