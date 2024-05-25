package me.khruslan.cryptograph.data.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.coins.local.PinnedCoinDto
import me.khruslan.cryptograph.data.coins.mapper.CoinsMapper
import me.khruslan.cryptograph.data.coins.mapper.CoinsMapperImpl
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
    fun `Maps coins`() = runTest {
        val pinnedCoins = listOf(PinnedCoinDto(coinUuid = COINS[1].id))
        val expectedCoins = listOf(COINS[1].copy(isPinned = true), COINS[0], COINS[2])
        val actualCoins = mapper.mapCoins(DTO_COINS, pinnedCoins)
        assertThat(actualCoins).isEqualTo(expectedCoins)
    }
}