package me.khruslan.cryptograph.data.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fakes.FakeCoinsRepository
import me.khruslan.cryptograph.data.fakes.FakeNotificationsRepository
import me.khruslan.cryptograph.data.interactors.sync.UpdateNotificationsInteractor
import me.khruslan.cryptograph.data.interactors.sync.UpdateNotificationsInteractorImpl
import org.junit.Before
import org.junit.Test

internal class UpdateNotificationsInteractorTests {

    private lateinit var fakeNotificationsRepository: FakeNotificationsRepository
    private lateinit var fakeCoinsRepository: FakeCoinsRepository
    private lateinit var interactor: UpdateNotificationsInteractor

    @Before
    fun setUp() {
        fakeNotificationsRepository = FakeNotificationsRepository()
        fakeCoinsRepository = FakeCoinsRepository()

        interactor = UpdateNotificationsInteractorImpl(
            notificationsRepository = fakeNotificationsRepository,
            coinsRepository = fakeCoinsRepository
        )
    }

    @Test
    fun `Update notifications - failed to load pending notifications`() = runTest {
        fakeNotificationsRepository.isDatabaseCorrupted = true
        val result = runCatching { interactor.updateNotifications() }

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `Update notifications - no pending notifications found`() = runTest {
        fakeNotificationsRepository.deleteAllNotifications()
        val result = runCatching { interactor.updateNotifications() }

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `Update notifications - failed to load coins`() = runTest {
        fakeCoinsRepository.isNetworkReachable = false
        val result = runCatching { interactor.updateNotifications() }

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `Update notifications - updated successfully`() = runTest {
        val result = runCatching { interactor.updateNotifications() }
        assertThat(result.isSuccess).isTrue()
    }
}