package me.khruslan.cryptograph.data.coins

import java.time.LocalDate

data class Coin(
    val id: String,
    val symbol: String?,
    val name: String,
    val colorHex: String?,
    val iconUrl: String?,
    val price: String?,
    val change: String?,
    val changeTrend: ChangeTrend,
    val rank: Int?,
    val sparkline: List<Double>,
    val isPinned: Boolean,
)

enum class ChangeTrend {
    UP,
    DOWN,
    STEADY_OR_UNKNOWN
}

data class CoinPrice(
    val price: Double,
    val date: LocalDate
)