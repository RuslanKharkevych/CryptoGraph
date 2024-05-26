package me.khruslan.cryptograph.data.tests

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.coins.CoinsRepositoryImpl
import me.khruslan.cryptograph.data.fakes.FakeCoinsMapper
import me.khruslan.cryptograph.data.fakes.FakeCoinsRemoteDataSource
import me.khruslan.cryptograph.data.fakes.FakeCoinsLocalDataSource
import me.khruslan.cryptograph.data.fixtures.STUB_COINS
import org.junit.Before
import org.junit.Test

internal class CoinsRepositoryTests {

    private lateinit var repository: CoinsRepository

    @Before
    fun setUp() {
        repository = CoinsRepositoryImpl(
            localDataSource = FakeCoinsLocalDataSource(),
            remoteDataSource = FakeCoinsRemoteDataSource(),
            mapper = FakeCoinsMapper()
        )
    }

    @Test
    fun `Get coins`() = runTest {
        repository.coins.test {
            assertThat(STUB_COINS).isEqualTo(awaitItem())
        }
    }

    @Test
    fun `Pin coin`() = runTest {
        val coinId = STUB_COINS[1].id
        repository.pinCoin(coinId)

        repository.coins.test {
            val expectedCoins = listOf(
                STUB_COINS[1].copy(isPinned = true),
                STUB_COINS[0],
                STUB_COINS[2]
            )
            val actualCoins = awaitItem()
            assertThat(actualCoins).isEqualTo(expectedCoins)
        }
    }

    @Test
    fun `Unpin coin`() = runTest {
        val coinId = STUB_COINS[2].id
        repository.pinCoin(coinId)
        repository.unpinCoin(coinId)

        repository.coins.test {
            assertThat(STUB_COINS).isEqualTo(awaitItem())
        }
    }
}