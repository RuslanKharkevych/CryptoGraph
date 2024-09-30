package me.khruslan.cryptograph.data.workers

import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

internal val workersModule = module {
    workerOf(::UpdateNotificationsWorker)
}