package me.khruslan.cryptograph.ui.notifications

import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsViewModel
import me.khruslan.cryptograph.ui.notifications.main.NotificationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

internal val notificationsModule = module {
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::NotificationDetailsViewModel)
}