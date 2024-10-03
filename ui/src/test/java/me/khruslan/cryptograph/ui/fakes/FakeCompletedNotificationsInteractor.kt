package me.khruslan.cryptograph.ui.fakes

import me.khruslan.cryptograph.data.interactors.notifications.completed.CompletedNotification
import me.khruslan.cryptograph.data.interactors.notifications.completed.CompletedNotificationsInteractor

internal class FakeCompletedNotificationsInteractor : CompletedNotificationsInteractor {
    var notificationsRefreshed = false

    override suspend fun getCompletedNotifications(): List<CompletedNotification> {
        throw UnsupportedOperationException()
    }

    override suspend fun tryRefreshCompletedNotifications() {
        notificationsRefreshed = true
    }
}