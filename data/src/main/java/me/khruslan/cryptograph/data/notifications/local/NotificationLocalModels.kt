package me.khruslan.cryptograph.data.notifications.local

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
internal data class NotificationDto(
    @Id var id: Long = 0,
    var coinUuid: String = "",
    var title: String = "",
    var createdAtDate: String = "",
    var completedAtDate: String? = null,
    var expirationDate: String? = null,
    var priceLessThanTrigger: Double? = null,
    var priceMoreThanTrigger: Double? = null,
    var finalized: Boolean = false,
)