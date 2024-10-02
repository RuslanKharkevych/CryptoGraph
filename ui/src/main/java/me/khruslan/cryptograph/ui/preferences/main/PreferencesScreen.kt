package me.khruslan.cryptograph.ui.preferences.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.khruslan.cryptograph.data.fixtures.PREVIEW_PREFERENCES
import me.khruslan.cryptograph.data.preferences.ChartPeriod
import me.khruslan.cryptograph.data.preferences.ChartStyle
import me.khruslan.cryptograph.data.preferences.Preferences
import me.khruslan.cryptograph.data.preferences.Theme
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.preferences.main.selection.PreferenceSelectionDialog
import me.khruslan.cryptograph.ui.util.ArrowDown
import me.khruslan.cryptograph.ui.util.ChoiceItems
import me.khruslan.cryptograph.ui.util.preview.PreviewScreenSizesLightDark
import me.khruslan.cryptograph.ui.util.components.FullScreenLoader
import me.khruslan.cryptograph.ui.util.state.rememberAlertState

@Composable
internal fun PreferencesScreen(
    preferencesState: PreferencesState,
    onThemeSelected: (theme: Theme) -> Unit,
    onChartStyleSelected: (chartStyle: ChartStyle) -> Unit,
    onChartPeriodSelected: (chartPeriod: ChartPeriod) -> Unit,
    onWarningShown: () -> Unit,
    onCloseActionClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    preferencesState.warningMessageRes?.let { resId ->
        val snackbarMessage = stringResource(resId)
        LaunchedEffect(snackbarMessage) {
            snackbarHostState.showSnackbar(snackbarMessage)
            onWarningShown()
        }
    }

    Scaffold(
        topBar = {
            TopBar(onCloseActionClick = onCloseActionClick)
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { contentPadding ->
        Crossfade(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            targetState = preferencesState.preferences,
            label = "PreferencesStateCrossfade"
        ) { preferences ->
            if (preferences == null) {
                FullScreenLoader()
            } else {
                PreferencesList(
                    preferences = preferences,
                    appVersion = preferencesState.appVersion,
                    onThemeSelected = onThemeSelected,
                    onChartStyleSelected = onChartStyleSelected,
                    onChartPeriodSelected = onChartPeriodSelected
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onCloseActionClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.preferences_top_bar_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(onClick = onCloseActionClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowDown,
                    contentDescription = stringResource(R.string.close_action_desc)
                )
            }
        }
    )
}

@Composable
private fun PreferencesList(
    preferences: Preferences,
    appVersion: String,
    onThemeSelected: (theme: Theme) -> Unit,
    onChartStyleSelected: (chartStyle: ChartStyle) -> Unit,
    onChartPeriodSelected: (chartPeriod: ChartPeriod) -> Unit,
) {
    val themeAlertState = rememberAlertState()
    val chartStyleAlertState = rememberAlertState()
    val chartPeriodAlertState = rememberAlertState()

    if (themeAlertState.isVisible) {
        PreferenceSelectionDialog(
            title = stringResource(R.string.select_theme_dialog_title),
            items = ChoiceItems.Themes,
            selectedItem = preferences.theme,
            onItemSelected = onThemeSelected,
            onDismiss = themeAlertState::dismiss
        )
    }

    if (chartStyleAlertState.isVisible) {
        PreferenceSelectionDialog(
            title = stringResource(R.string.select_chart_style_dialog_title),
            items = ChoiceItems.ChartStyles,
            selectedItem = preferences.chartStyle,
            onItemSelected = onChartStyleSelected,
            onDismiss = chartStyleAlertState::dismiss
        )
    }

    if (chartPeriodAlertState.isVisible) {
        PreferenceSelectionDialog(
            title = stringResource(R.string.select_chart_period_dialog_title),
            items = ChoiceItems.ChartPeriods,
            selectedItem = preferences.chartPeriod,
            onItemSelected = onChartPeriodSelected,
            onDismiss = chartPeriodAlertState::dismiss
        )
    }

    Column {
        PreferenceItem(
            title = stringResource(R.string.theme_preference_title),
            onClick = themeAlertState::show
        )
        PreferenceItem(
            title = stringResource(R.string.chart_style_preference_title),
            onClick = chartStyleAlertState::show
        )
        PreferenceItem(
            title = stringResource(R.string.chart_period_preference_title),
            onClick = chartPeriodAlertState::show
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
            text = "v$appVersion",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PreferenceItem(title: String, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = title,
                fontWeight = FontWeight.Medium
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = null
            )
        }
    )
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
@PreviewScreenSizesLightDark
private fun PreferencesScreenPreview() {
    val appVersion = "1.0.0"
    val preferencesState = remember {
        MutablePreferencesState(appVersion).apply {
            preferences = PREVIEW_PREFERENCES
        }
    }

    CryptoGraphTheme {
        PreferencesScreen(
            preferencesState = preferencesState,
            onThemeSelected = {},
            onChartStyleSelected = {},
            onChartPeriodSelected = {},
            onWarningShown = {},
            onCloseActionClick = {}
        )
    }
}