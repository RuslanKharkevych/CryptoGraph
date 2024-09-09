package me.khruslan.cryptograph.data.fakes

import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.CoinPrice
import me.khruslan.cryptograph.data.coins.local.PinnedCoinDto
import me.khruslan.cryptograph.data.coins.mapper.CoinsMapper
import me.khruslan.cryptograph.data.coins.remote.CoinDto
import me.khruslan.cryptograph.data.coins.remote.CoinPriceDto
import me.khruslan.cryptograph.data.fixtures.STUB_COINS
import me.khruslan.cryptograph.data.fixtures.STUB_COIN_HISTORY

internal class FakeCoinsMapper : CoinsMapper {

    override suspend fun mapCoins(
        allCoins: List<CoinDto?>,
        pinnedCoins: List<PinnedCoinDto>,
    ): List<Coin> {
        return allCoins.map { coinDto ->
            val isPinned = pinnedCoins.any { it.coinUuid == coinDto?.uuid }
            STUB_COINS.first { it.id == coinDto?.uuid }.copy(isPinned = isPinned)
        }.sortedWith(compareByDescending<Coin> { it.isPinned }.thenBy { it.rank })
    }

    override suspend fun mapCoinHistory(history: List<CoinPriceDto?>): List<CoinPrice> {
        return STUB_COIN_HISTORY
    }
}