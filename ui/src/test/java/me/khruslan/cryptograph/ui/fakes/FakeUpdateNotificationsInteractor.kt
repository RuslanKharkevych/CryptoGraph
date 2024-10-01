package me.khruslan.cryptograph.ui.fakes

import me.khruslan.cryptograph.data.interactors.sync.UpdateNotificationsInteractor

internal class FakeUpdateNotificationsInteractor : UpdateNotificationsInteractor {
    var notificationsUpdated = false

    override suspend fun updateNotifications() {
        notificationsUpdated = true
    }
}