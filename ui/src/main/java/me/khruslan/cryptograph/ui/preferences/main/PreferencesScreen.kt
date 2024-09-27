package me.khruslan.cryptograph.ui.preferences.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import me.khruslan.cryptograph.data.fixtures.STUB_PREFERENCES
import me.khruslan.cryptograph.data.preferences.ChartPeriod
import me.khruslan.cryptograph.data.preferences.ChartStyle
import me.khruslan.cryptograph.data.preferences.Theme
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.util.ArrowDown
import me.khruslan.cryptograph.ui.util.PreviewScreenSizesLightDark
import me.khruslan.cryptograph.ui.util.components.FullScreenLoader

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
                // TODO: Build preferences screen UI
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
@PreviewScreenSizesLightDark
private fun PreferencesScreenPreview() {
    val preferencesState = remember {
        MutablePreferencesState().apply {
            preferences = STUB_PREFERENCES
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