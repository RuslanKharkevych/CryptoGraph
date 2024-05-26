package me.khruslan.cryptograph.data.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.coins.local.PinnedCoinDto
import me.khruslan.cryptograph.data.coins.mapper.CoinsMapper
import me.khruslan.cryptograph.data.coins.mapper.CoinsMapperImpl
import me.khruslan.cryptograph.data.coins.remote.CoinDto
import me.khruslan.cryptograph.data.common.DataValidationException
import me.khruslan.cryptograph.data.fixtures.COINS
import me.khruslan.cryptograph.data.fixtures.DTO_COINS
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class CoinsMapperTests {

    private lateinit var mapper: CoinsMapper

    @Before
    fun setUp() {
        val dispatcher = UnconfinedTestDispatcher()
        mapper = CoinsMapperImpl(dispatcher)
    }

    @Test
    fun `Map coins - success`() = runTest {
        val pinnedCoins = listOf(PinnedCoinDto(coinUuid = COINS[1].id))
        val expectedCoins = listOf(COINS[1].copy(isPinned = true), COINS[0], COINS[2])
        val actualCoins = mapper.mapCoins(DTO_COINS, pinnedCoins)
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
}