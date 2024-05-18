package me.khruslan.cryptograph.data.coins.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.coins.ChangeTrend
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.local.PinnedCoinDto
import me.khruslan.cryptograph.data.coins.remote.CoinDto
import java.math.BigDecimal
import java.math.RoundingMode

private const val LOG_TAG = "CoinsMapper"
private const val COLOR_HEX_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})\$"

internal interface CoinsMapper {
    suspend fun mapCoins(
        allCoins: List<CoinDto>,
        pinnedCoins: List<PinnedCoinDto>,
    ): List<Coin>
}

internal class CoinsMapperImpl(private val dispatcher: CoroutineDispatcher) : CoinsMapper {
    override suspend fun mapCoins(
        allCoins: List<CoinDto>,
        pinnedCoins: List<PinnedCoinDto>,
    ): List<Coin> {
        return withContext(dispatcher) {
            allCoins.map { coinDto ->
                val isPinned = pinnedCoins.any { it.coinUuid == coinDto.uuid }
                mapCoin(coinDto, isPinned)
            }.sortedWith(compareByDescending<Coin> { it.isPinned }.thenBy { it.rank })
        }
    }

    private fun mapCoin(coinDto: CoinDto, isPinned: Boolean): Coin {
        return Coin(
            id = coinDto.uuid,
            symbol = coinDto.symbol,
            name = coinDto.name,
            colorHex = coinDto.color?.let(::mapColorHex),
            iconUrl = coinDto.iconUrl,
            price = mapPrice(coinDto.price),
            change = mapChange(coinDto.change),
            changeTrend = mapChangeTrend(coinDto.change),
            rank = coinDto.rank,
            sparkline = mapSparkline(coinDto.sparkline),
            isPinned = isPinned
        )
    }

    private fun mapColorHex(color: String): String? {
        return if (Regex(COLOR_HEX_PATTERN).matches(color)) {
            color
        } else {
            Logger.debug(LOG_TAG, "Failed to map color HEX: $color")
            null
        }
    }

    private fun mapChange(change: String): String {
        return "$change%"
    }

    private fun mapChangeTrend(change: String): ChangeTrend {
        return try {
            val floatValue = change.toFloat()
            when {
                floatValue > 0f -> ChangeTrend.UP
                floatValue < 0f -> ChangeTrend.DOWN
                else -> ChangeTrend.STEADY_OR_UNKNOWN
            }
        } catch (e: NumberFormatException) {
            Logger.error(LOG_TAG, "Failed to map change trend: $change", e)
            ChangeTrend.STEADY_OR_UNKNOWN
        }
    }

    private fun mapPrice(price: String): String {
        val formattedNumber = try {
            var scale = 2
            if (price.startsWith('0')) {
                for (c in price.substringAfter('.', "")) {
                    if (c == '0') scale++ else break
                }
            }

            BigDecimal(price)
                .setScale(scale, RoundingMode.HALF_UP)
                .toPlainString()
        } catch (e: NumberFormatException) {
            Logger.error(LOG_TAG, "Failed to map price: $price", e)
            price
        }

        return "\$$formattedNumber"
    }

    private fun mapSparkline(sparkline: List<String?>): List<Double> {
        val floatSparkline = sparkline.mapNotNull { it?.toDoubleOrNull() }
        val minValue = floatSparkline.minOrNull() ?: 0.0
        return floatSparkline.map { it - minValue * 0.99 }
    }
}