package me.khruslan.cryptograph.data.coins.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.coins.ChangeTrend
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.CoinPrice
import me.khruslan.cryptograph.data.coins.local.PinnedCoinDto
import me.khruslan.cryptograph.data.coins.remote.CoinDto
import me.khruslan.cryptograph.data.coins.remote.CoinPriceDto
import me.khruslan.cryptograph.data.common.DataValidationException
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

private const val LOG_TAG = "CoinsMapper"
private const val COLOR_HEX_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})\$"

internal class CoinsMapper(private val dispatcher: CoroutineDispatcher) {

    suspend fun mapCoins(
        allCoins: List<CoinDto?>,
        pinnedCoins: List<PinnedCoinDto>,
    ): List<Coin> {
        return withContext(dispatcher) {
            try {
                allCoins.filterNotNull().mapNotNull { coinDto ->
                    val validationScope = CoinValidationScope(coinDto)
                    val isPinned = pinnedCoins.any { it.coinUuid == coinDto.uuid }
                    validationScope.mapCoin(coinDto, isPinned)
                }.sorted().also { mappedCoins ->
                    require(mappedCoins.isNotEmpty()) { "Mapped coins list is empty" }
                }
            } catch (e: IllegalArgumentException) {
                Logger.error(LOG_TAG, "Failed to map $allCoins", e)
                throw DataValidationException(e)
            }
        }
    }

    suspend fun mapCoinHistory(history: List<CoinPriceDto?>): List<CoinPrice> {
        return withContext(dispatcher) {
            try {
                history.filterNotNull().mapNotNull { coinPriceDto ->
                    mapCoinPrice(coinPriceDto)
                }.also { mappedHistory ->
                    require(mappedHistory.isNotEmpty()) { "Mapped history is empty" }
                }
            } catch (e: IllegalArgumentException) {
                Logger.error(LOG_TAG, "Failed to map $history", e)
                throw DataValidationException(e)
            }
        }
    }

    private fun CoinValidationScope.mapCoin(coinDto: CoinDto, isPinned: Boolean): Coin? {
        return try {
            Coin(
                id = requireNotBlank(coinDto.uuid) { "`uuid` is null or blank" },
                symbol = expectNotBlank(coinDto.symbol) { "`symbol` is null or blank" },
                name = requireNotBlank(coinDto.name) { "`name` is null or blank" },
                colorHex = mapColorHex(coinDto.color),
                iconUrl = expectNotBlank(coinDto.iconUrl) { "`iconUrl` is null or blank" },
                price = mapPrice(coinDto.price),
                change = mapChange(coinDto.change),
                changeTrend = mapChangeTrend(coinDto.change),
                rank = expectNotNull(coinDto.rank) { "`rank` is null" },
                sparkline = mapSparkline(coinDto.sparkline),
                isPinned = isPinned
            )
        } catch (e: IllegalArgumentException) {
            Logger.error(LOG_TAG, "Failed to map $coinDto", e)
            null
        } finally {
            close()
        }
    }

    private fun mapColorHex(color: String?): String? {
        return if (color != null && Regex(COLOR_HEX_PATTERN).matches(color)) {
            color
        } else {
            Logger.info(LOG_TAG, "Failed to map color HEX: $color")
            null
        }
    }

    private fun CoinValidationScope.mapChange(change: String?): String? {
        return withScope(defaultValue = null) {
            requireNotBlank(change) { "`change` is null or blank" }
            "$change%"
        }
    }

    private fun CoinValidationScope.mapChangeTrend(change: String?): ChangeTrend {
        if (change.isNullOrBlank()) {
            return ChangeTrend.STEADY_OR_UNKNOWN
        }

        return withScope(defaultValue = ChangeTrend.STEADY_OR_UNKNOWN) {
            try {
                val floatValue = change.toFloat()
                when {
                    floatValue > 0f -> ChangeTrend.UP
                    floatValue < 0f -> ChangeTrend.DOWN
                    else -> ChangeTrend.STEADY_OR_UNKNOWN
                }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("`change` has invalid format", e)
            }
        }
    }

    private fun CoinValidationScope.mapPrice(price: String?): String? {
        return withScope(defaultValue = price?.ifBlank { null }) {
            requireNotBlank(price) { "`price` is null or blank" }

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
                throw IllegalArgumentException("`price` has invalid format", e)
            }

            "\$$formattedNumber"
        }
    }

    private fun CoinValidationScope.mapSparkline(sparkline: List<String?>?): List<Double> {
        return withScope(defaultValue = emptyList()) {
            requireNotNull(sparkline) { "`sparkline` is null" }
            val doubleSparkline = sparkline.mapNotNull { it?.toDoubleOrNull() }
            require(doubleSparkline.size > 1) { "`sparkline` has invalid format" }
            val minValue = doubleSparkline.minOrNull() ?: 0.0
            doubleSparkline.map { it - minValue * 0.99 }
        }
    }

    private fun mapCoinPrice(coinPriceDto: CoinPriceDto): CoinPrice? {
        if (coinPriceDto.price.isNullOrBlank()) {
            Logger.debug(LOG_TAG, "Skipped $coinPriceDto")
            return null
        }

        return try {
            CoinPrice(
                price = mapPrice(coinPriceDto.price),
                date = mapTimestamp(coinPriceDto.timestamp)
            )
        } catch (e: IllegalArgumentException) {
            Logger.warning(LOG_TAG, "Failed to parse $coinPriceDto", e)
            return null
        }
    }

    private fun mapPrice(price: String): Double {
        return try {
            price.toDouble()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("`price` has invalid format")
        }
    }

    private fun mapTimestamp(timestamp: Long?): LocalDate {
        requireNotNull(timestamp) { "`timestamp` is null" }
        return try {
            val instant = Instant.ofEpochSecond(timestamp)
            instant.atZone(ZoneOffset.UTC).toLocalDate()
        } catch (e: DateTimeException) {
            throw IllegalArgumentException("`timestamp` has invalid format")
        }
    }

    private fun List<Coin>.sorted(): List<Coin> {
        return sortedWith(compareByDescending<Coin> { it.isPinned }.thenBy { it.rank })
    }

    private class CoinValidationScope(private val coinDto: CoinDto) {
        private val errors = mutableListOf<Throwable>()

        inline fun <T> withScope(defaultValue: T, validationBlock: () -> T): T {
            return runCatching {
                validationBlock()
            }.onFailure { error ->
                errors += error
            }.getOrDefault(defaultValue)
        }

        inline fun <T> expectNotNull(value: T?, errorMessage: () -> Any): T? {
            return withScope(defaultValue = null) {
                requireNotNull(value, errorMessage)
            }
        }

        inline fun expectNotBlank(value: String?, errorMessage: () -> Any): String? {
            return withScope(defaultValue = null) {
                requireNotBlank(value, errorMessage)
            }
        }

        fun close() {
            if (errors.isNotEmpty()) logErrors()
        }

        private fun logErrors() {
            val errorMessage = "Unexpected conditions during mapping $coinDto"
            val exception = IllegalArgumentException(errors.joinToString { it.message.orEmpty() })
            Logger.warning(LOG_TAG, errorMessage, exception)
        }
    }
}

@OptIn(ExperimentalContracts::class)
private inline fun requireNotBlank(value: String?, errorMessage: () -> Any): String {
    contract {
        returns() implies (value != null)
    }

    if (value.isNullOrBlank()) {
        val message = errorMessage()
        throw IllegalArgumentException(message.toString())
    } else {
        return value
    }
}