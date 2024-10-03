package me.khruslan.cryptograph.data.interactors.notifications.completed

import me.khruslan.cryptograph.data.notifications.NotificationTrigger

data class CompletedNotification(
    val title: String,
    val coinName: String,
    val trigger: NotificationTrigger
)