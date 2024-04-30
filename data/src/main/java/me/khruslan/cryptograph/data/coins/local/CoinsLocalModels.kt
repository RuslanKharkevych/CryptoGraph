package me.khruslan.cryptograph.data.coins.local

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
internal data class PinnedCoinDto(
    @Id var id: Long = 0,
    var coinUuid: String = ""
)