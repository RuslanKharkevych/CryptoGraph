package me.khruslan.cryptograph.data.interactors

import kotlinx.coroutines.Dispatchers
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.interactors.notifications.coin.CoinNotificationsInteractor
import me.khruslan.cryptograph.data.interactors.notifications.coin.CoinNotificationsInteractorImpl
import me.khruslan.cryptograph.data.interactors.notifications.coin.CoinNotificationsMapper
import me.khruslan.cryptograph.data.interactors.notifications.completed.CompletedNotificationsInteractor
import me.khruslan.cryptograph.data.interactors.notifications.completed.CompletedNotificationsInteractorImpl
import me.khruslan.cryptograph.data.notifications.NotificationsRepository
import org.koin.dsl.module

internal val interactorsModule = module {
    single<CoinNotificationsInteractor> {
        CoinNotificationsInteractorImpl(
            coinsRepository = get<CoinsRepository>(),
            notificationsRepository = get<NotificationsRepository>(),
            mapper = CoinNotificationsMapper(
                dispatcher = Dispatchers.Default
            )
        )
    }
    single<CompletedNotificationsInteractor> {
        CompletedNotificationsInteractorImpl(
            notificationsRepository = get<NotificationsRepository>(),
            coinsRepository = get<CoinsRepository>()
        )
    }
}