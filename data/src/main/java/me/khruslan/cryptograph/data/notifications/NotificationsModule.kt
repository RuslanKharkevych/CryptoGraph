package me.khruslan.cryptograph.data.notifications

import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import kotlinx.coroutines.Dispatchers
import me.khruslan.cryptograph.data.common.objectBoxModule
import me.khruslan.cryptograph.data.notifications.local.NotificationsLocalDataSourceImpl
import me.khruslan.cryptograph.data.notifications.mapper.NotificationsMapper
import org.koin.dsl.module
import java.time.Clock

internal val notificationsModule = module {
    includes(objectBoxModule)
    single<NotificationsRepository> {
        NotificationsRepositoryImpl(
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
}