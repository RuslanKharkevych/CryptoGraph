package me.khruslan.cryptograph.data.coins.remote

import kotlinx.serialization.Serializable

@Serializable
internal data class CoinrankingResponse<T>(val data: T)

@Serializable
internal data class CoinsDto(val coins: List<CoinDto>)

// TODO: Make all fields optional. In mapper, either assign a default value (if field can be
//  optional), or skip a coin (if field is absolutely mandatory).
@Serializable
internal data class CoinDto(
    val uuid: String,
    val symbol: String,
    val name: String,
    val color: String?,
    val iconUrl: String,
    val price: String,
    val change: String = "", // TODO: Remove default value after mapping updates
    val rank: Int,
    val sparkline: List<String?>,
)

@Serializable
internal data class CoinHistoryDto(val history: List<CoinPriceDto>)

@Serializable
internal data class CoinPriceDto(
    val price: String?,
    val timestamp: Long
)