package me.khruslan.cryptograph.data.coins

data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val color: String?,
    val iconUrl: String,
    val price: String,
    val change: String,
    val rank: Int,
    val sparkline: List<Float>,
    val coinrankingUrl: String,
    val isPinned: Boolean
)