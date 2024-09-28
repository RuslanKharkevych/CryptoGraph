package me.khruslan.cryptograph.ui.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fixtures.STUB_PREFERENCES
import me.khruslan.cryptograph.data.preferences.Theme
import me.khruslan.cryptograph.ui.core.CryptoGraphViewModel
import me.khruslan.cryptograph.ui.fakes.FakePreferencesRepository
import me.khruslan.cryptograph.ui.rules.MainDispatcherRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class CryptoGraphViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakePreferencesRepository: FakePreferencesRepository
    private lateinit var viewModel: CryptoGraphViewModel

    @Before
    fun setUp() {
        fakePreferencesRepository = FakePreferencesRepository()
        viewModel = CryptoGraphViewModel(fakePreferencesRepository)
    }

    @Test
    fun `Load theme`() {
        val expectedTheme = STUB_PREFERENCES.theme
        val actualTheme = viewModel.appState.theme

        assertThat(actualTheme).isEqualTo(expectedTheme)
    }

    @Test
    fun `Observe theme`() = runTest {
        val theme = Theme.Light
        fakePreferencesRepository.updateTheme(theme)

        assertThat(viewModel.appState.theme).isEqualTo(theme)
    }
}