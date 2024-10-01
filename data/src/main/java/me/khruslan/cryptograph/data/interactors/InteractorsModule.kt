package me.khruslan.cryptograph.data.interactors

import kotlinx.coroutines.Dispatchers
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.interactors.combine.CoinNotificationsInteractor
import me.khruslan.cryptograph.data.interactors.combine.CoinNotificationsInteractorImpl
import me.khruslan.cryptograph.data.interactors.combine.CoinNotificationsMapper
import me.khruslan.cryptograph.data.interactors.sync.UpdateNotificationsInteractor
import me.khruslan.cryptograph.data.interactors.sync.UpdateNotificationsInteractorImpl
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
    single<UpdateNotificationsInteractor> {
        UpdateNotificationsInteractorImpl(
            notificationsRepository = get<NotificationsRepository>(),
            coinsRepository = get<CoinsRepository>()
        )
    }
}