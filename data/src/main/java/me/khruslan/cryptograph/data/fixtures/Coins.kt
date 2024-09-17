package me.khruslan.cryptograph.data.fixtures

import me.khruslan.cryptograph.data.coins.ChangeTrend
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.CoinPrice
import me.khruslan.cryptograph.data.coins.remote.CoinDto
import me.khruslan.cryptograph.data.coins.remote.CoinPriceDto
import java.time.LocalDate

val PREVIEW_COINS
    get() = STUB_COINS + listOf(
        Coin(
            id = "WcwrkfNI4FUAe",
            symbol = "BNB",
            name = "BNB",
            colorHex = "#e8b342",
            iconUrl = "https://cdn.coinranking.com/B1N19L_dZ/bnb.svg",
            price = "593.01",
            rank = 4,
            sparkline = listOf(
                603.505093363307,
                599.6097994976376,
                601.414256733252,
                600.1661876777998,
                598.6508399298144,
                593.0011195226035,
                594.2848201883025,
                594.6049201418058,
                590.7466219200281,
                586.3156889152173,
                586.3878368349378,
                587.4930781093288,
                588.6042641367998,
                586.3735827106152,
                586.9463698599395,
                587.4155390160329,
                588.4153114122097,
                590.7252052567412,
                591.8602029088246,
                591.6851823060163,
                590.8851500169487,
                590.4714923716019,
                591.8639863870972
            ),
            change = "-1.77%",
            changeTrend = ChangeTrend.DOWN,
            isPinned = false
        ),
        Coin(
            id = "zNZHO_Sjf",
            symbol = "SOL",
            name = "Solana",
            colorHex = null,
            iconUrl = "https://cdn.coinranking.com/yvUG4Qex5/solana.svg",
            price = "136.43",
            rank = 5,
            sparkline = listOf(
                140.22188254904697,
                138.0771102662861,
                138.31383497661054,
                138.26631979758724,
                137.49913300517554,
                136.17747367495423,
                136.02074992644305,
                136.1030812245628,
                135.56921933766574,
                134.98507549686835,
                134.54848947855066,
                135.13456638961645,
                135.8832718892764,
                134.9857035879123,
                135.16815426810615,
                135.48138274209668,
                136.05562111539317,
                136.4027608926906,
                135.49189946216526,
                134.72803600113403,
                134.40725788873957,
                134.34939513439036,
                135.17945636829518
            ),
            change = "-3.05%",
            changeTrend = ChangeTrend.DOWN,
            isPinned = false
        ),
        Coin(
            id = "aKzUVe4Hh_CON",
            symbol = "USDC",
            name = "USDC",
            colorHex = null,
            iconUrl = "https://cdn.coinranking.com/jkDf8sQbY/usdc.svg",
            price = "1.00",
            rank = 6,
            sparkline = listOf(
                1.0011380690133656,
                1.0011332368991817,
                1.0005226288834013,
                1.0006913841742726,
                1.001344611073512,
                1.0007791420757781,
                1.0003007701142141,
                1.0004294974362762,
                1.0008945449314197,
                1.0005009179388034,
                1.0003327096307617,
                1.0002790844921474,
                1.0003819289636404,
                1.0006594513182543,
                1.0004023016242112,
                1.000798510178642,
                1.0000829028441143,
                1.0004428582324671,
                1.000843079997,
                1.0004049339712107,
                1.0007123333822687,
                1.0007332633559747,
                1.0007011034925304
            ),
            change = "0.00%",
            changeTrend = ChangeTrend.STEADY_OR_UNKNOWN,
            isPinned = false
        )
    )

val STUB_COINS: List<Coin>
    get() = listOf(
        Coin(
            id = "Qwsogvtv82FCd",
            symbol = "BTC",
            name = "Bitcoin",
            colorHex = "#f7931A",
            iconUrl = "https://cdn.coinranking.com/bOabBYkcX/bitcoin_btc.svg",
            price = "$63374.15",
            rank = 1,
            sparkline = listOf(
                1761.5760022910981,
                1364.7579643024583,
                1618.0100674692076,
                1606.7549416864495,
                1435.5545462645314,
                1001.0554563301339,
                891.2724150593349,
                790.9025636857623,
                674.8016004192323,
                623.1355157317594,
                765.602073581591,
                776.1928436954404,
                927.5038348098606,
                737.6118039189969,
                777.7991469524495,
                764.2070884411005,
                884.6351356494488,
                1298.7833620400124,
                1358.9932298122658,
                1378.439188447046,
                1283.055266849682,
                1131.121356425865,
                1412.7252158475167
            ),
            change = "0.33%",
            changeTrend = ChangeTrend.UP,
            isPinned = false
        ),
        Coin(
            id = "razxDUgYGNAdQ",
            symbol = "ETH",
            name = "Ethereum",
            colorHex = "#3C3C3D",
            iconUrl = "https://cdn.coinranking.com/rk4RKHOuW/eth.svg",
            price = "$3192.03",
            rank = 2,
            sparkline = listOf(
                184.8548244601975,
                152.98656409760724,
                161.68686359898993,
                161.7489449989921,
                137.90400573863326,
                87.72134375656105,
                78.94620958486166,
                80.17308770081308,
                71.57254983876237,
                67.56743202363987,
                60.761215310851185,
                56.54755342534099,
                67.66310651236972,
                51.9954413157825,
                47.80395885725966,
                31.47968627377668,
                41.65971130337675,
                59.46481648825147,
                57.35981284387208,
                52.956781142417185,
                54.367788769913204,
                54.46014125213105,
                66.1157041116403
            ),
            change = "3.48%",
            changeTrend = ChangeTrend.UP,
            isPinned = false
        ),
        Coin(
            id = "HIVsRcGKkPFtW",
            symbol = "USDT",
            name = "Tether USD",
            colorHex = "#22a079",
            iconUrl = "https://cdn.coinranking.com/mgHqwlCLj/usdt.svg",
            price = "$1.00",
            rank = 3,
            sparkline = listOf(
                0.010609349721644401,
                0.010602725670259883,
                0.010283960983068052,
                0.01037398315791016,
                0.011082912211909601,
                0.010834507069306332,
                0.010512039414376728,
                0.010616203870325469,
                0.010980403605894695,
                0.010465649446081082,
                0.010688301696552216,
                0.010575542848508368,
                0.010641923046475599,
                0.010684637702780941,
                0.010408523284936466,
                0.010952803167585623,
                0.01000154717947499,
                0.01048212091630707,
                0.010798359615303621,
                0.010424462609062823,
                0.010580504782494726,
                0.01061208762421284,
                0.010495978745968193
            ),
            change = "-0.11%",
            changeTrend = ChangeTrend.DOWN,
            isPinned = false
        ),
    )

internal val STUB_DTO_COINS
    get() = listOf(
        CoinDto(
            uuid = "Qwsogvtv82FCd",
            symbol = "BTC",
            name = "Bitcoin",
            color = "#f7931A",
            iconUrl = "https://cdn.coinranking.com/bOabBYkcX/bitcoin_btc.svg",
            price = "63374.153154686035",
            rank = 1,
            sparkline = listOf(
                "63451.99205973526",
                "63055.17402174662",
                "63308.42612491337",
                "63297.17099913061",
                "63125.970603708694",
                "62691.4715137743",
                "62581.6884725035",
                "62481.318621129925",
                "62365.217657863395",
                "62313.55157317592",
                "62456.018131025754",
                "62466.6089011396",
                "62617.91989225402",
                "62428.02786136316",
                "62468.21520439661",
                "62454.62314588526",
                "62575.05119309361",
                "62989.199419484175",
                "63049.40928725643",
                "63068.85524589121",
                "62973.471324293845",
                "62821.53741387003",
                "63103.14127329168",
                null
            ),
            change = "0.33"
        ),
        CoinDto(
            uuid = "razxDUgYGNAdQ",
            symbol = "ETH",
            name = "Ethereum",
            color = "#3C3C3D",
            iconUrl = "https://cdn.coinranking.com/rk4RKHOuW/eth.svg",
            price = "3192.0288414672605",
            rank = 2,
            sparkline = listOf(
                "3301.3437655641046",
                "3269.4755052015144",
                "3278.175804702897",
                "3278.237886102899",
                "3254.3929468425404",
                "3204.210284860468",
                "3195.4351506887688",
                "3196.66202880472",
                "3188.0614909426695",
                "3184.056373127547",
                "3177.2501564147583",
                "3173.036494529248",
                "3184.152047616277",
                "3168.4843824196896",
                "3164.2928999611668",
                "3147.968627377684",
                "3158.148652407284",
                "3175.9537575921586",
                "3173.848753947779",
                "3169.4457222463243",
                "3170.8567298738203",
                "3170.949082356038",
                "3182.6046452155474",
                null
            ),
            change = "3.48"
        ),
        CoinDto(
            uuid = "HIVsRcGKkPFtW",
            symbol = "USDT",
            name = "Tether USD",
            color = "#22a079",
            iconUrl = "https://cdn.coinranking.com/mgHqwlCLj/usdt.svg",
            price = "0.9995386699159282",
            rank = 3,
            sparkline = listOf(
                "1.00076252048967",
                "1.0007558964382854",
                "1.0004371317510936",
                "1.0005271539259357",
                "1.0012360829799352",
                "1.000987677837332",
                "1.0006652101824023",
                "1.000769374638351",
                "1.0011335743739203",
                "1.0006188202141066",
                "1.0008414724645778",
                "1.000728713616534",
                "1.0007950938145012",
                "1.0008378084708065",
                "1.000561694052962",
                "1.0011059739356112",
                "1.0001547179475005",
                "1.0006352916843326",
                "1.0009515303833292",
                "1.0005776333770884",
                "1.0007336755505203",
                "1.0007652583922384",
                "1.0006491495139938",
                null
            ),
            change = "-0.11"
        )
    )

val PREVIEW_COIN_HISTORY
    get() = List(5 * 365) { day ->
        val date = LocalDate.now().minusDays(day.toLong())
        val randomizer = date.dayOfWeek.value / date.dayOfMonth.toDouble()

        CoinPrice(
            date = date,
            price = 40_000.00 + 20_000.00 * randomizer
        )
    }

val STUB_COIN_HISTORY
    get() = listOf(
        CoinPrice(
            price = 69290.58547403838,
            date = LocalDate.of(2024, 5, 26)
        ),
        CoinPrice(
            price = 68548.86589620463,
            date = LocalDate.of(2024, 5, 25)
        ),
        CoinPrice(
            price = 67958.47091146026,
            date = LocalDate.of(2024, 5, 24)
        )
    )

internal val STUB_DTO_COIN_HISTORY
    get() = listOf(
        CoinPriceDto(
            price = "69290.58547403838",
            timestamp = 1716681600L
        ),
        CoinPriceDto(
            price = "68548.86589620463",
            timestamp = 1716595200L
        ),
        CoinPriceDto(
            price = "67958.47091146026",
            timestamp = 1716508800L
        )
    )