package me.khruslan.cryptograph.data.notifications.repository.local

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
internal data class NotificationDto(
    @Id var id: Long = 0,
    var coinUuid: String = "",
    var title: String = "",
    var createdAtDateTime: String = "",
    var completedAtDate: String? = null,
    var expirationDate: String? = null,
    var priceLessThanTrigger: Double? = null,
    var priceMoreThanTrigger: Double? = null,
)