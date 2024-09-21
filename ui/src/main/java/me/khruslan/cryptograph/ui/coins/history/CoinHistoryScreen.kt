package me.khruslan.cryptograph.ui.coins.history

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.axisGuidelineComponent
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.chart.Chart
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.marker.Marker
import me.khruslan.cryptograph.data.coins.CoinPrice
import me.khruslan.cryptograph.data.fixtures.PREVIEW_COIN_HISTORY
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.coins.history.chart.PriceMarkerComponent
import me.khruslan.cryptograph.ui.coins.history.chart.PriceMarkerLabelFormatter
import me.khruslan.cryptograph.ui.coins.shared.PinCoinButton
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.util.PreviewScreenSizesLightDark
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.components.FullScreenError
import me.khruslan.cryptograph.ui.util.components.FullScreenLoader
import me.khruslan.cryptograph.ui.util.toColor
import me.khruslan.cryptograph.ui.util.typeface

@Composable
internal fun CoinHistoryScreen(
    coinHistoryState: CoinHistoryState,
    onPinActionClick: () -> Unit,
    onUnpinActionClick: () -> Unit,
    onRetryClick: () -> Unit,
    onWarningShown: () -> Unit,
    onBackActionClick: () -> Unit,
    onNotificationsActionClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    coinHistoryState.warningMessageRes?.let { resId ->
        val snackbarMessage = stringResource(resId)
        LaunchedEffect(snackbarMessage) {
            snackbarHostState.showSnackbar(snackbarMessage)
            onWarningShown()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = coinHistoryState.coinName,
                isPinned = coinHistoryState.isPinned,
                onBackActionClick = onBackActionClick,
                onNotificationsActionClick = onNotificationsActionClick,
                onPinActionClick = onPinActionClick,
                onUnpinActionClick = onUnpinActionClick
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { contentPadding ->
        Crossfade(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            targetState = coinHistoryState.chartState,
            label = "CoinChartStateCrossfade"
        ) { chartState ->
            when (chartState) {
                is UiState.Loading -> FullScreenLoader()

                is UiState.Data -> PriceChart(
                    color = coinHistoryState.colorHex.toColor(),
                    prices = chartState.data
                )

                is UiState.Error -> FullScreenError(
                    message = stringResource(chartState.messageRes),
                    onRetryClick = onRetryClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    title: String,
    isPinned: Boolean,
    onBackActionClick: () -> Unit,
    onNotificationsActionClick: () -> Unit,
    onPinActionClick: () -> Unit,
    onUnpinActionClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                modifier = Modifier.basicMarquee(),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackActionClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_action_desc)
                )
            }
        },
        actions = {
            IconButton(onClick = onNotificationsActionClick) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.notifications_action_desc)
                )
            }
            PinCoinButton(
                isPinned = isPinned,
                onPin = onPinActionClick,
                onUnpin = onUnpinActionClick
            )
        }
    )
}

@Composable
private fun PriceChart(
    color: Color,
    prices: List<CoinPrice>,
) {
    val chartState = rememberCoinHistoryChartState(prices)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        FilterChips(
            color = color.copy(alpha = 0.35f),
            selectedStyle = chartState.style,
            selectedPeriod = chartState.period,
            onStyleSelected = chartState::updateStyle,
            onPeriodSelected = chartState::updatePeriod
        )

        Chart(
            modifier = Modifier
                .fillMaxHeight(0.85f)
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            chart = rememberChart(
                style = chartState.style,
                color = color
            ),
            model = chartState.model,
            isZoomEnabled = false,
            chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
            bottomAxis = rememberBottomAxis(
                dateFormatter = chartState.dateFormatter,
                spacing = chartState.bottomAxisSpacing,
                addExtremeLabelPadding = chartState.style == CoinHistoryChartStyle.ColumnChart
            ),
            horizontalLayout = HorizontalLayout.FullWidth(),
            marker = rememberMarker(
                chartStyle = chartState.style,
                indicatorColor = color
            )
        )
    }
}

@Composable
private fun FilterChips(
    color: Color,
    selectedStyle: CoinHistoryChartStyle,
    selectedPeriod: CoinHistoryChartPeriod,
    onStyleSelected: (CoinHistoryChartStyle) -> Unit,
    onPeriodSelected: (CoinHistoryChartPeriod) -> Unit,
) {
    val styleFilterChips = @Composable {
        StyleFilterChips(
            color = color,
            selectedStyle = selectedStyle,
            onStyleSelected = onStyleSelected
        )
    }

    val periodFilterChips = @Composable {
        PeriodFilterChips(
            color = color,
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodSelected
        )
    }

    BoxWithConstraints {
        if (maxWidth > 800.dp || maxHeight < 400.dp) {
            Row {
                styleFilterChips()
                periodFilterChips()
            }
        } else {
            Column {
                styleFilterChips()
                Spacer(modifier = Modifier.height(16.dp))
                periodFilterChips()
            }
        }
    }
}

@Composable
private fun StyleFilterChips(
    color: Color,
    selectedStyle: CoinHistoryChartStyle,
    onStyleSelected: (CoinHistoryChartStyle) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.style_label),
            style = MaterialTheme.typography.bodyLarge
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CoinHistoryChartStyle.entries.forEach { style ->
                FilterChip(
                    selected = style == selectedStyle,
                    onClick = { onStyleSelected(style) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color
                    ),
                    label = {
                        Text(
                            text = stringResource(style.nameRes),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PeriodFilterChips(
    color: Color,
    selectedPeriod: CoinHistoryChartPeriod,
    onPeriodSelected: (CoinHistoryChartPeriod) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.period_label),
            style = MaterialTheme.typography.bodyLarge
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CoinHistoryChartPeriod.entries.forEach { period ->
                FilterChip(
                    selected = period == selectedPeriod,
                    onClick = { onPeriodSelected(period) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color
                    ),
                    label = {
                        Text(
                            text = stringResource(period.nameRes),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun rememberChart(style: CoinHistoryChartStyle, color: Color): Chart<ChartEntryModel> {
    return when (style) {
        CoinHistoryChartStyle.LineChart -> lineChart(
            lines = listOf(
                lineSpec(
                    lineColor = color,
                    lineBackgroundShader = DynamicShaders.fromBrush(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.5f),
                                color.copy(alpha = 0.15f)
                            ),
                        ),
                    )
                )
            )
        )

        CoinHistoryChartStyle.ColumnChart -> columnChart(
            columns = listOf(
                lineComponent(
                    color = color.copy(alpha = 0.5f),
                    strokeColor = color,
                    strokeWidth = 2.dp
                )
            ),
            spacing = 2.dp
        )
    }
}

@Composable
private fun rememberBottomAxis(
    dateFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>,
    spacing: Int,
    addExtremeLabelPadding: Boolean,
): HorizontalAxis<AxisPosition.Horizontal.Bottom> {
    val labelTextStyle = MaterialTheme.typography.labelSmall
    val label = axisLabelComponent(
        textSize = labelTextStyle.fontSize,
        typeface = labelTextStyle.typeface ?: currentChartStyle.axis.axisLabelTypeface
    )

    val itemPlacer = remember(spacing, addExtremeLabelPadding) {
        AxisItemPlacer.Horizontal.default(
            spacing = spacing,
            addExtremeLabelPadding = addExtremeLabelPadding
        )
    }

    return rememberBottomAxis(
        label = label,
        valueFormatter = dateFormatter,
        guideline = null,
        itemPlacer = itemPlacer
    )
}

@Composable
private fun rememberMarker(chartStyle: CoinHistoryChartStyle, indicatorColor: Color): Marker {
    val labelTextStyle = MaterialTheme.typography.labelSmall
    val label = textComponent(
        color = MaterialTheme.colorScheme.onBackground,
        textSize = labelTextStyle.fontSize,
        typeface = labelTextStyle.typeface
    )
    val guideline = axisGuidelineComponent()
    val indicator = remember(chartStyle) {
        ShapeComponent(
            shape = Shapes.pillShape,
            color = indicatorColor.toArgb()
        ).takeIf { chartStyle == CoinHistoryChartStyle.LineChart }
    }

    return remember(indicator) {
        PriceMarkerComponent(
            label = label,
            indicator = indicator,
            guideline = guideline
        ).apply {
            indicatorSizeDp = 8f
            labelFormatter = PriceMarkerLabelFormatter()
        }
    }
}

@Composable
@PreviewScreenSizesLightDark
private fun CoinHistoryScreenPreview() {
    val coinHistoryState = remember {
        val args = CoinHistoryArgs(
            coinId = "Qwsogvtv82FCd",
            coinName = "Bitcoin",
            colorHex = "#F7931A",
            isPinned = false
        )

        MutableCoinHistoryState(args).apply {
            chartState = UiState.Data(data = PREVIEW_COIN_HISTORY)
        }
    }

    CryptoGraphTheme {
        CoinHistoryScreen(
            coinHistoryState = coinHistoryState,
            onPinActionClick = { coinHistoryState.isPinned = true },
            onUnpinActionClick = { coinHistoryState.isPinned = false },
            onRetryClick = {},
            onWarningShown = {},
            onBackActionClick = {},
            onNotificationsActionClick = {}
        )
    }
}