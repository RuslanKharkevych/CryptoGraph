package me.khruslan.cryptograph.data.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.coins.local.PinnedCoinDto
import me.khruslan.cryptograph.data.coins.mapper.CoinsMapper
import me.khruslan.cryptograph.data.coins.remote.CoinDto
import me.khruslan.cryptograph.data.coins.remote.CoinPriceDto
import me.khruslan.cryptograph.data.core.DataValidationException
import me.khruslan.cryptograph.data.fixtures.STUB_COINS
import me.khruslan.cryptograph.data.fixtures.STUB_COIN_HISTORY
import me.khruslan.cryptograph.data.fixtures.STUB_DTO_COINS
import me.khruslan.cryptograph.data.fixtures.STUB_DTO_COIN_HISTORY
import org.junit.Before
import org.junit.Test

internal class CoinsMapperTests {

    private lateinit var mapper: CoinsMapper

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        val dispatcher = UnconfinedTestDispatcher()
        mapper = CoinsMapper(dispatcher)
    }

    @Test
    fun `Map coins - success`() = runTest {
        val pinnedCoins = listOf(PinnedCoinDto(coinUuid = STUB_COINS[1].id))
        val expectedCoins = listOf(
            STUB_COINS[1].copy(isPinned = true),
            STUB_COINS[0],
            STUB_COINS[2]
        )
        val actualCoins = mapper.mapCoins(STUB_DTO_COINS, pinnedCoins)
        assertThat(actualCoins).isEqualTo(expectedCoins)
    }

    @Test
    fun `Map coins - validation error`() = runTest {
        val allCoins = emptyList<CoinDto>()
        val pinnedCoins = emptyList<PinnedCoinDto>()
        val result = runCatching { mapper.mapCoins(allCoins, pinnedCoins) }
        val exception = result.exceptionOrNull()
        assertThat(exception).isInstanceOf(DataValidationException::class.java)
    }

    @Test
    fun `Map coin history - success`() = runTest {
        val history = mapper.mapCoinHistory(STUB_DTO_COIN_HISTORY)
        assertThat(history).isEqualTo(STUB_COIN_HISTORY)
    }

    @Test
    fun `Map coin history - validation error`() = runTest {
        val history = listOf(
            CoinPriceDto(price = null, timestamp = 1716681600L),
            CoinPriceDto(price = "invalid-price", timestamp = 1716595200L),
            CoinPriceDto(price = "67958.47091146026", timestamp = Long.MAX_VALUE)
        )
        val result = runCatching { mapper.mapCoinHistory(history) }
        val exception = result.exceptionOrNull()
        assertThat(exception).isInstanceOf(DataValidationException::class.java)
    }
}