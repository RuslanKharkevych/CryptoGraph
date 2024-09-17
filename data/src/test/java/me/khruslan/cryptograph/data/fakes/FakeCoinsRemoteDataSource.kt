package me.khruslan.cryptograph.data.fakes

import me.khruslan.cryptograph.data.coins.remote.CoinDto
import me.khruslan.cryptograph.data.coins.remote.CoinPriceDto
import me.khruslan.cryptograph.data.coins.remote.CoinsRemoteDataSource
import me.khruslan.cryptograph.data.fixtures.STUB_DTO_COINS
import me.khruslan.cryptograph.data.fixtures.STUB_DTO_COIN_HISTORY

internal class FakeCoinsRemoteDataSource : CoinsRemoteDataSource {

    override suspend fun getCoins(uuid: String?): List<CoinDto> {
        return STUB_DTO_COINS.filter { uuid?.equals(it.uuid) ?: true }
    }

    override suspend fun getCoinHistory(uuid: String): List<CoinPriceDto> {
        return STUB_DTO_COIN_HISTORY
    }
}