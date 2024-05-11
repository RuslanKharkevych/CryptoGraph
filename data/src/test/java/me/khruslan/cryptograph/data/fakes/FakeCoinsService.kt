package me.khruslan.cryptograph.data.fakes

import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.remote.CoinDto
import me.khruslan.cryptograph.data.coins.remote.CoinsRemoteDataSource

internal class FakeCoinsService : CoinsRemoteDataSource {
    override suspend fun getCoins() = STUB_DTO_COINS
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

private val STUB_DTO_COINS = listOf(
    CoinDto(
        uuid = "Qwsogvtv82FCd",
        symbol = "BTC",
        name = "Bitcoin",
        color = "#F7931A",
        iconUrl = "https://cdn.coinranking.com/bOabBYkcX/bitcoin_btc.svg",
        price = "63374.153154686035",
        rank = 1,
        sparkline = listOf("63451.99205973526", "63055.17402174662", null),
        change = "-0.33",
        coinrankingUrl = "https://coinranking.com/coin/Qwsogvtv82FCd+bitcoin-btc"
    ),
    CoinDto(
        uuid = "razxDUgYGNAdQ",
        symbol = "ETH",
        name = "Ethereum",
        color = "#3C3C3D",
        iconUrl = "https://cdn.coinranking.com/rk4RKHOuW/eth.svg",
        price = "3192.0288414672605",
        rank = 2,
        sparkline = listOf("3301.3437655641046"),
        change = "-3.48",
        coinrankingUrl = "https://coinranking.com/coin/razxDUgYGNAdQ+ethereum-eth"
    ),
    CoinDto(
        uuid = "HIVsRcGKkPFtW",
        symbol = "USDT",
        name = "Tether USD",
        color = "#22A079",
        iconUrl = "https://cdn.coinranking.com/mgHqwlCLj/usdt.svg",
        price = "0.9995386699159282",
        rank = 3,
        sparkline = listOf("1.00076252048967", "1.0007558964382854", "1.0004371317510936"),
        change = "-0.11",
        coinrankingUrl = "https://coinranking.com/coin/HIVsRcGKkPFtW+tetherusd-usdt"
    )
)