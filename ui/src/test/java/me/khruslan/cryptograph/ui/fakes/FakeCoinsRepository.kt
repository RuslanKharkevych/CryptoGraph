package me.khruslan.cryptograph.ui.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.common.ErrorType

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
            }
        }
    }

    override suspend fun unpinCoin(id: String) {
        checkIfDataIsValid()
        _coins.update { coins ->
            coins.map {
                it.copy(isPinned = if (it.id == id) false else it.isPinned)
            }
        }
    }

    private fun checkIfNetworkIsReachable() {
        if (!isNetworkReachable) throw object : DataException(ErrorType.Network) {}
    }

    private fun checkIfDataIsValid() {
        if (isDatabaseCorrupted) throw object : DataException(ErrorType.Database) {}
    }
}

internal val STUB_COINS = listOf(
    Coin(
        id = "Qwsogvtv82FCd",
        symbol = "BTC",
        name = "Bitcoin",
        color = "#F7931A",
        iconUrl = "https://cdn.coinranking.com/bOabBYkcX/bitcoin_btc.svg",
        price = "63374.153154686035",
        rank = 1,
        sparkline = listOf(63451.992f, 63055.176f),
        change = "-0.33",
        coinrankingUrl = "https://coinranking.com/coin/Qwsogvtv82FCd+bitcoin-btc",
        isPinned = false
    ),
    Coin(
        id = "razxDUgYGNAdQ",
        symbol = "ETH",
        name = "Ethereum",
        color = "#3C3C3D",
        iconUrl = "https://cdn.coinranking.com/rk4RKHOuW/eth.svg",
        price = "3192.0288414672605",
        rank = 2,
        sparkline = listOf(3301.3438f),
        change = "-3.48",
        coinrankingUrl = "https://coinranking.com/coin/razxDUgYGNAdQ+ethereum-eth",
        isPinned = false
    ),
    Coin(
        id = "HIVsRcGKkPFtW",
        symbol = "USDT",
        name = "Tether USD",
        color = "#22A079",
        iconUrl = "https://cdn.coinranking.com/mgHqwlCLj/usdt.svg",
        price = "0.9995386699159282",
        rank = 3,
        sparkline = listOf(1.0007625f, 1.0007559f, 1.0004371f),
        change = "-0.11",
        coinrankingUrl = "https://coinranking.com/coin/HIVsRcGKkPFtW+tetherusd-usdt",
        isPinned = false
    )
)