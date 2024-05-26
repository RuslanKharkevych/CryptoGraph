package me.khruslan.cryptograph.data.fakes

import me.khruslan.cryptograph.data.coins.remote.CoinsRemoteDataSource
import me.khruslan.cryptograph.data.fixtures.STUB_DTO_COINS
import me.khruslan.cryptograph.data.fixtures.STUB_DTO_COIN_HISTORY

internal class FakeCoinsRemoteDataSource : CoinsRemoteDataSource {
    override suspend fun getCoins() = STUB_DTO_COINS
    override suspend fun getCoinHistory(uuid: String) = STUB_DTO_COIN_HISTORY
}