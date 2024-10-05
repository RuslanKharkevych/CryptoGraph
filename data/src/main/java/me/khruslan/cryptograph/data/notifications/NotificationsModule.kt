package me.khruslan.cryptograph.data.notifications

import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import kotlinx.coroutines.Dispatchers
import me.khruslan.cryptograph.base.LaunchOptions
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.notifications.managers.PushNotificationsManager
import me.khruslan.cryptograph.data.notifications.interactors.coin.CoinNotificationsInteractor
import me.khruslan.cryptograph.data.notifications.interactors.coin.CoinNotificationsInteractorImpl
import me.khruslan.cryptograph.data.notifications.interactors.coin.CoinNotificationsMapper
import me.khruslan.cryptograph.data.notifications.interactors.completed.CompletedNotificationsInteractor
import me.khruslan.cryptograph.data.notifications.interactors.completed.CompletedNotificationsInteractorImpl
import me.khruslan.cryptograph.data.notifications.interactors.completed.CompletedNotificationsMapper
import me.khruslan.cryptograph.data.notifications.repository.NotificationsRepository
import me.khruslan.cryptograph.data.notifications.repository.NotificationsRepositoryImpl
import me.khruslan.cryptograph.data.notifications.repository.local.NotificationsLocalDataSourceImpl
import me.khruslan.cryptograph.data.notifications.repository.mapper.NotificationsMapper
import me.khruslan.cryptograph.data.notifications.workers.PostCompletedNotificationsWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.scope.Scope
import org.koin.dsl.module
import java.time.Clock

internal val notificationsModule = module {
    single { notificationsRepository() }
    single { coinNotificationsInteractor() }
    single { completedNotificationsInteractor() }
    single { pushNotificationsManager() }
    workerOf(::PostCompletedNotificationsWorker)
}

private fun Scope.notificationsRepository(): NotificationsRepository {
    return NotificationsRepositoryImpl(
        localDataSource = NotificationsLocalDataSourceImpl(
            box = get<BoxStore>().boxFor(),
            dispatcher = Dispatchers.IO
        ),
        mapper = NotificationsMapper(
            dispatcher = Dispatchers.Default,
            clock = Clock.systemDefaultZone(),
        )
    )
}

private fun Scope.coinNotificationsInteractor(): CoinNotificationsInteractor {
    return CoinNotificationsInteractorImpl(
        coinsRepository = get<CoinsRepository>(),
        notificationsRepository = get<NotificationsRepository>(),
        mapper = CoinNotificationsMapper(
            dispatcher = Dispatchers.Default
        )
    )
}

private fun Scope.completedNotificationsInteractor(): CompletedNotificationsInteractor {
    return CompletedNotificationsInteractorImpl(
        notificationsRepository = get<NotificationsRepository>(),
        coinsRepository = get<CoinsRepository>(),
        mapper = CompletedNotificationsMapper(
            clock = Clock.systemDefaultZone()
        )
    )
}

private fun Scope.pushNotificationsManager(): PushNotificationsManager {
    return PushNotificationsManager(
        context = androidContext(),
        launchOptions = get<LaunchOptions>()
    )
}