package me.khruslan.cryptograph.ui.coins.history.chart

import android.graphics.RectF
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.core.chart.values.ChartValuesProvider
import com.patrykandpatrick.vico.core.component.Component
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.cornered.MarkerCorneredShape
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.component.text.VerticalPosition
import com.patrykandpatrick.vico.core.context.DrawContext
import com.patrykandpatrick.vico.core.extension.ceil
import com.patrykandpatrick.vico.core.extension.doubled
import com.patrykandpatrick.vico.core.extension.half
import com.patrykandpatrick.vico.core.extension.orZero
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter

private const val LABEL_MARGIN_DP = 8f

internal class PriceMarkerComponent(
    label: TextComponent,
    indicator: Component?,
    guideline: LineComponent?,
) : MarkerComponent(label, indicator, guideline) {

    private val tempBounds = RectF()

    private val TextComponent.tickSizeDp: Float
        get() {
            val background = background as? ShapeComponent
            val shape = background?.shape as? MarkerCorneredShape
            return shape?.tickSizeDp.orZero
        }

    override fun draw(
        context: DrawContext,
        bounds: RectF,
        markedEntries: List<Marker.EntryModel>,
        chartValuesProvider: ChartValuesProvider,
    ) {
        with(context) {
            drawGuideline(bounds, markedEntries)
            drawIndicator(markedEntries)
            drawLabel(bounds, markedEntries, chartValuesProvider.getChartValues())
        }
    }

    private fun DrawContext.drawGuideline(
        bounds: RectF,
        markedEntries: List<Marker.EntryModel>,
    ) {
        markedEntries.forEach { entry ->
            guideline?.drawVertical(
                context = this,
                top = bounds.top,
                bottom = entry.location.y,
                centerX = entry.location.x
            )
        }
    }

    private fun DrawContext.drawIndicator(markedEntries: List<Marker.EntryModel>) {
        val halfIndicatorSize = indicatorSizeDp.half.pixels
        markedEntries.forEachIndexed { _, model ->
            onApplyEntryColor?.invoke(model.color)
            indicator?.draw(
                context = this,
                left = model.location.x - halfIndicatorSize,
                top = model.location.y - halfIndicatorSize,
                right = model.location.x + halfIndicatorSize,
                bottom = model.location.y + halfIndicatorSize,
            )
        }
    }

    private fun DrawContext.drawLabel(
        bounds: RectF,
        markedEntries: List<Marker.EntryModel>,
        chartValues: ChartValues,
    ) {
        val text = labelFormatter.getLabel(markedEntries, chartValues)
        val entryX = markedEntries.averageOf { it.location.x }
        val labelBounds = label.getTextBounds(
            context = this,
            text = text,
            width = bounds.width().toInt(),
            outRect = tempBounds
        )
        val halfOfTextWidth = labelBounds.width().half
        val x = overrideXPositionToFit(entryX, bounds, halfOfTextWidth)
        val y = bounds.top - labelBounds.height() - label.tickSizeDp.pixels - LABEL_MARGIN_DP.pixels
        this[MarkerCorneredShape.tickXKey] = entryX

        label.drawText(
            context = this,
            text = text,
            textX = x,
            textY = y,
            verticalPosition = VerticalPosition.Bottom,
            maxTextWidth = minOf(bounds.right - x, x - bounds.left).doubled.ceil.toInt(),
        )
    }

    private fun overrideXPositionToFit(
        xPosition: Float,
        bounds: RectF,
        halfOfTextWidth: Float,
    ): Float {
        return when {
            xPosition - halfOfTextWidth < bounds.left -> bounds.left + halfOfTextWidth
            xPosition + halfOfTextWidth > bounds.right -> bounds.right - halfOfTextWidth
            else -> xPosition
        }
    }

    private fun <T> Collection<T>.averageOf(selector: (T) -> Float): Float {
        return fold(0f) { sum, element -> sum + selector(element) } / size
    }
}

internal class PriceMarkerLabelFormatter : MarkerLabelFormatter {
    override fun getLabel(
        markedEntries: List<Marker.EntryModel>,
        chartValues: ChartValues,
    ): CharSequence {
        val entryModel = markedEntries.firstOrNull() ?: return ""
        return entryModel.entry.y.toBigDecimal().toPlainString()
    }
}