package me.khruslan.cryptograph.data.notifications.local

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
internal data class NotificationDto(
    @Id var id: Long = 0,
    var coinUuid: String = "",
    var title: String = "",
    var createdAtTimestamp: Long = 0L,
    var expirationDateTimestamp: Long? = null,
    var priceLessThenTrigger: Double? = null,
    var priceMoreThenTrigger: Double? = null,
)