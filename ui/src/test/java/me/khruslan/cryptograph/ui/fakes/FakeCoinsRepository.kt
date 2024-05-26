package me.khruslan.cryptograph.ui.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.common.ErrorType
import me.khruslan.cryptograph.data.fixtures.STUB_COINS

internal class FakeCoinsRepository : CoinsRepository {

    var isNetworkReachable = true
    var isDatabaseCorrupted = false

    private val _coins = MutableStateFlow(STUB_COINS)

    override val coins: Flow<List<Coin>>
        get() {
            checkIfNetworkIsReachable()
            return _coins
        }

    override suspend fun pinCoin(id: String) {
        checkIfDataIsValid()
        _coins.update { coins ->
            coins.map {
                it.copy(isPinned = if (it.id == id) true else it.isPinned)
            }.sorted()
        }
    }

    override suspend fun unpinCoin(id: String) {
        checkIfDataIsValid()
        _coins.update { coins ->
            coins.map {
                it.copy(isPinned = if (it.id == id) false else it.isPinned)
            }.sorted()
        }
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