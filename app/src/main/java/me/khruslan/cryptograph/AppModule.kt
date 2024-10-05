package me.khruslan.cryptograph

import me.khruslan.cryptograph.base.AppVersion
import me.khruslan.cryptograph.base.LaunchOptions
import me.khruslan.cryptograph.ui.core.CryptoGraphActivity
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal val appModule = module {
    single { appVersion() }
    single { launchOptions() }
}

private fun appVersion(): AppVersion {
    return AppVersion(BuildConfig.VERSION_NAME)
}

private fun Scope.launchOptions(): LaunchOptions {
    return LaunchOptions(
        notificationIntent = CryptoGraphActivity.newNotificationIntent(
            context = androidContext()
        )
    )
}