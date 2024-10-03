package me.khruslan.cryptograph

import me.khruslan.cryptograph.base.AppVersion
import me.khruslan.cryptograph.base.LaunchOptions
import me.khruslan.cryptograph.ui.core.CryptoGraphActivity
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val appModule = module {
    single {
        AppVersion(
            name = BuildConfig.VERSION_NAME
        )
    }
    single {
        LaunchOptions(
            notificationIntent = CryptoGraphActivity.newNotificationIntent(
                context = androidContext()
            )
        )
    }
}