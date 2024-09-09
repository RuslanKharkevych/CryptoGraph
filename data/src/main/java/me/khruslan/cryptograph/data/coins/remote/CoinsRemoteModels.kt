package me.khruslan.cryptograph.data.coins.remote

import kotlinx.serialization.Serializable

@Serializable
internal data class CoinrankingResponse<T>(val data: T)

@Serializable
internal data class CoinsDto(val coins: List<CoinDto?>)

@Serializable
internal data class CoinDto(
    val uuid: String?,
    val symbol: String?,
    val name: String?,
    val color: String?,
    val iconUrl: String?,
    val price: String?,
    val change: String?,
    val rank: Int?,
    val sparkline: List<String?>?,
)

@Serializable
internal data class CoinHistoryDto(val history: List<CoinPriceDto?>)

@Serializable
internal data class CoinPriceDto(
    val price: String?,
    val timestamp: Long?
)