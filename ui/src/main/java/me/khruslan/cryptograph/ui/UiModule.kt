package me.khruslan.cryptograph.ui

import me.khruslan.cryptograph.ui.coins.coinsModule
import me.khruslan.cryptograph.ui.notifications.notificationsModule
import me.khruslan.cryptograph.ui.preferences.preferencesModule
import org.koin.dsl.module

val uiModule = module {
    includes(coinsModule, notificationsModule, preferencesModule)
}