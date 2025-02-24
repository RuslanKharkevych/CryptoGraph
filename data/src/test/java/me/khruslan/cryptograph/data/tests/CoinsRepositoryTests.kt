package me.khruslan.cryptograph.data.tests

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.coins.CoinsRepositoryImpl
import me.khruslan.cryptograph.data.coins.mapper.CoinsMapper
import me.khruslan.cryptograph.data.fakes.FakeCoinsRemoteDataSource
import me.khruslan.cryptograph.data.fakes.FakeCoinsLocalDataSource
import me.khruslan.cryptograph.data.fixtures.STUB_COINS
import me.khruslan.cryptograph.data.fixtures.STUB_COIN_HISTORY
import org.junit.Before
import org.junit.Test
import java.util.UUID

internal class CoinsRepositoryTests {

    private lateinit var repository: CoinsRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        val dispatcher = UnconfinedTestDispatcher()

        repository = CoinsRepositoryImpl(
            localDataSource = FakeCoinsLocalDataSource(),
            remoteDataSource = FakeCoinsRemoteDataSource(),
            mapper = CoinsMapper(dispatcher)
        )
    }

    @Test
    fun `Get coins`() = runTest {
        repository.getCoins().test {
            assertThat(awaitItem()).isEqualTo(STUB_COINS)
        }
    }

    @Test
    fun `Filter coins`() = runTest {
        val coin = STUB_COINS[0]
        repository.getCoins(coin.id).test {
            assertThat(awaitItem()).containsExactly(coin)
        }
    }

    @Test
    fun `Pin coin`() = runTest {
        val coinId = STUB_COINS[1].id
        repository.pinCoin(coinId)

        repository.getCoins().test {
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

        repository.getCoins().test {
            assertThat(awaitItem()).isEqualTo(STUB_COINS)
        }
    }

    @Test
    fun `Get coin history`() = runTest {
        val coinId = UUID.randomUUID().toString()
        val history = repository.getCoinHistory(coinId)
        assertThat(history).isEqualTo(STUB_COIN_HISTORY)
    }
}