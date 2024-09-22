package me.khruslan.cryptograph.ui.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.CoinPrice
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.common.ErrorType
import me.khruslan.cryptograph.data.fixtures.STUB_COINS
import me.khruslan.cryptograph.data.fixtures.STUB_COIN_HISTORY

internal class FakeCoinsRepository : CoinsRepository {

    var isNetworkReachable = true
    var isDatabaseCorrupted = false

    private val coins = MutableStateFlow(STUB_COINS)

    override fun getCoins(id: String?): Flow<List<Coin>> {
        checkIfNetworkIsReachable()
        return coins.map { coins ->
            coins.filter { id?.equals(it.id) ?: true }
        }
    }

    override suspend fun pinCoin(id: String) {
        checkIfDataIsValid()
        coins.update { coins ->
            coins.map {
                it.copy(isPinned = if (it.id == id) true else it.isPinned)
            }.sorted()
        }
    }

    override suspend fun unpinCoin(id: String) {
        checkIfDataIsValid()
        coins.update { coins ->
            coins.map {
                it.copy(isPinned = if (it.id == id) false else it.isPinned)
            }.sorted()
        }
    }

    override suspend fun getCoinHistory(id: String): List<CoinPrice> {
        checkIfNetworkIsReachable()
        return STUB_COIN_HISTORY
    }

    private fun checkIfNetworkIsReachable() {
        if (!isNetworkReachable) throw object : DataException(ErrorType.Network) {}
    }

    private fun checkIfDataIsValid() {
        if (isDatabaseCorrupted) throw object : DataException(ErrorType.Database) {}
    }

    private fun List<Coin>.sorted(): List<Coin> {
        return sortedWith(compareByDescending<Coin> { it.isPinned }.thenBy { it.rank })
    }
}