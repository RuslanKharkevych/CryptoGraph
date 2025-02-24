package me.khruslan.cryptograph.data.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.CoinPrice
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.core.DataException
import me.khruslan.cryptograph.data.core.ErrorType
import me.khruslan.cryptograph.data.fixtures.STUB_COINS

internal class FakeCoinsRepository : CoinsRepository {

    var isNetworkReachable = true

    override fun getCoins(id: String?): Flow<List<Coin>> {
        checkIfNetworkIsReachable()
        return flowOf(STUB_COINS.filter { id?.equals(it.id) ?: true })
    }

    override suspend fun pinCoin(id: String) {
        throw UnsupportedOperationException()
    }

    override suspend fun unpinCoin(id: String) {
        throw UnsupportedOperationException()
    }

    override suspend fun getCoinHistory(id: String): List<CoinPrice> {
        throw UnsupportedOperationException()
    }

    private fun checkIfNetworkIsReachable() {
        if (!isNetworkReachable) throw object : DataException(ErrorType.Network) {}
    }
}