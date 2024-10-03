package me.khruslan.cryptograph.data.managers

import me.khruslan.cryptograph.base.LaunchOptions
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val managersModule = module {
    single {
        PushNotificationsManager(
            context = androidContext(),
            launchOptions = get<LaunchOptions>()
        )
    }
}