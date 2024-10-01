package me.khruslan.cryptograph.ui.fakes

import me.khruslan.cryptograph.data.interactors.notifications.completed.CompletedNotificationsInteractor
import me.khruslan.cryptograph.data.notifications.Notification

internal class FakeCompletedNotificationsInteractor : CompletedNotificationsInteractor {
    var notificationsUpdated = false

    override suspend fun getCompletedNotifications(): List<Notification> {
        throw UnsupportedOperationException()
    }

    override suspend fun tryRefreshCompletedNotifications() {
        notificationsUpdated = true
    }
}