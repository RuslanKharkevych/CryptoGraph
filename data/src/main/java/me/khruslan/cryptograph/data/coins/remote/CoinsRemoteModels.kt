package me.khruslan.cryptograph.data.coins.remote

import kotlinx.serialization.Serializable
import me.khruslan.cryptograph.data.coins.Coin

@Serializable
internal data class CoinrankingResponse<T>(val data: T)

@Serializable
internal data class CoinsDto(val coins: List<CoinDto>)

@Serializable
internal data class CoinDto(
    val uuid: String,
    val symbol: String,
    val name: String,
    val color: String?,
    val iconUrl: String,
    val price: String,
    val change: String,
    val rank: Int,
    val sparkline: List<String?>,
    val coinrankingUrl: String,
)

internal fun CoinDto.toCoin(isPinned: Boolean): Coin {
    return Coin(
        id = uuid,
        symbol = symbol,
        name = name,
        color = color,
        iconUrl = iconUrl,
        price = price,
        change = change,
        rank = rank,
        sparkline = sparkline.mapNotNull { it?.toFloatOrNull() },
        coinrankingUrl = coinrankingUrl,
        isPinned = isPinned
    )
}