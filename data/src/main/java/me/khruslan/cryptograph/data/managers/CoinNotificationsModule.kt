package me.khruslan.cryptograph.data.managers

import kotlinx.coroutines.Dispatchers
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.coins.coinsModule
import me.khruslan.cryptograph.data.notifications.NotificationsRepository
import me.khruslan.cryptograph.data.notifications.notificationsModule
import org.koin.dsl.module

internal val coinNotificationsModule = module {
    includes(coinsModule, notificationsModule)
    single<CoinNotificationsManager> {
        CoinNotificationsManagerImpl(
            coinsRepository = get<CoinsRepository>(),
            notificationsRepository = get<NotificationsRepository>(),
            mapper = CoinNotificationsMapperImpl(
                dispatcher = Dispatchers.Default
            )
        )
    }
}