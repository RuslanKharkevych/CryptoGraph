package me.khruslan.cryptograph

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import me.khruslan.cryptograph.data.dataModule
import me.khruslan.cryptograph.ui.core.buildImageLoader
import me.khruslan.cryptograph.ui.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

internal class CryptoGraphApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(level = if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE)
            androidContext(this@CryptoGraphApplication)
            modules(appModule, dataModule, uiModule)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return buildImageLoader(this)
    }
}