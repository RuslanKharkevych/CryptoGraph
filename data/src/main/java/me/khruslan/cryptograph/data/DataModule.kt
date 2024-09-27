package me.khruslan.cryptograph.data

import me.khruslan.cryptograph.data.managers.coinNotificationsModule
import me.khruslan.cryptograph.data.preferences.preferencesModule
import org.koin.dsl.module

val dataModule = module {
    includes(coinNotificationsModule)
    includes(preferencesModule)
}