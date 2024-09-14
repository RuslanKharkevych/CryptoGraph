package me.khruslan.cryptograph.data

import me.khruslan.cryptograph.data.coins.coinsModule
import me.khruslan.cryptograph.data.notifications.notificationsModule
import org.koin.dsl.module

val dataModule = module {
    includes(coinsModule, notificationsModule)
}