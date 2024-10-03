package me.khruslan.cryptograph.data.notifications.interactors.completed

import me.khruslan.cryptograph.data.notifications.repository.NotificationTrigger

data class CompletedNotification(
    val title: String,
    val coinName: String,
    val trigger: NotificationTrigger
)