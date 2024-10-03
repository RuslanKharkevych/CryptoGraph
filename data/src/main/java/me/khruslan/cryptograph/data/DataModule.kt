package me.khruslan.cryptograph.data

import me.khruslan.cryptograph.data.coins.coinsModule
import me.khruslan.cryptograph.data.core.coreModule
import me.khruslan.cryptograph.data.interactors.interactorsModule
import me.khruslan.cryptograph.data.managers.managersModule
import me.khruslan.cryptograph.data.notifications.notificationsModule
import me.khruslan.cryptograph.data.preferences.preferencesModule
import me.khruslan.cryptograph.data.workers.workersModule
import org.koin.dsl.module

// TODO: Refactor data modules to be
//  includes(coreModule, coinsModule, notificationsModule, preferencesModule)
val dataModule = module {
    includes(
        coreModule,
        coinsModule,
        notificationsModule,
        preferencesModule,
        interactorsModule,
        managersModule,
        workersModule
    )
}