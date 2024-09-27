package me.khruslan.cryptograph.ui.preferences.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import me.khruslan.cryptograph.ui.util.navigation.modal
import me.khruslan.cryptograph.ui.util.navigation.rememberNavInterceptor
import org.koin.androidx.compose.koinViewModel

private const val PREFERENCES_ROUTE = "preferences"

internal fun NavGraphBuilder.preferencesScreen(
    onBackActionClick: () -> Unit,
) {
    modal(PREFERENCES_ROUTE) { navBackStackEntry ->
        val viewModel: PreferencesViewModel = koinViewModel()
        val navInterceptor = rememberNavInterceptor(navBackStackEntry)

        PreferencesScreen(
            preferencesState = viewModel.preferencesState,
            onThemeSelected = viewModel::updateTheme,
            onChartStyleSelected = viewModel::updateChartStyle,
            onChartPeriodSelected = viewModel::updateChartPeriod,
            onWarningShown = viewModel::warningShown,
            onCloseActionClick = navInterceptor(onBackActionClick)
        )
    }
}

internal fun NavController.navigateToPreferences() {
    navigate(PREFERENCES_ROUTE)
}