package me.khruslan.cryptograph.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import me.khruslan.cryptograph.ui.CryptoGraphTheme
import me.khruslan.cryptograph.ui.R
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val TAU = 2 * PI.toFloat()

@Composable
internal fun FullScreenLoader() {
    val dotColorsByAngle = rememberDotColorsByAngle()
    val progressAngle by animateProgressAngle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize(0.5f),
            contentDescription = stringResource(R.string.loader_desc)
        ) {
            val maxDotRadius = size.minDimension / 10
            val loaderRadius = size.minDimension / 2 - maxDotRadius

            dotColorsByAngle.forEach { (angle, color) ->
                val offset = Offset(
                    x = loaderRadius * cos(angle),
                    y = loaderRadius * sin(angle)
                )

                val delta = minOf(
                    abs(progressAngle - angle - TAU),
                    abs(progressAngle - angle),
                    abs(progressAngle - angle + TAU)
                )
                val scale = 1 - delta / TAU
                val dotRadius = maxDotRadius * scale.coerceAtLeast(0.5f)

                drawCircle(color, dotRadius, center + offset)
            }
        }
    }
}

@Composable
private fun rememberDotColorsByAngle(): Map<Float, Color> {
    return remember {
        val dotAngles = (0 until 360 step 45).map { degrees ->
            Math.toRadians(degrees.toDouble()).toFloat()
        }

        dotAngles.zip(
            listOf(
                Color(0xFFF7931A),
                Color(0xFF22A079),
                Color(0xFFE8B342),
                Color(0xFF7894B4),
                Color(0xFF41C0f5),
                Color(0xFF0088CC),
                Color(0xFFE84242),
                Color(0xFF8DC451),
            )
        ).toMap()
    }
}

@Composable
private fun animateProgressAngle(): State<Float> {
    val currentAngleTransition = rememberInfiniteTransition(
        label = "FullScreenLoaderProgressAngleTransition"
    )

    return currentAngleTransition.animateFloat(
        label = "FullScreenLoaderProgressAngleAnimation",
        initialValue = 0f,
        targetValue = TAU,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        )
    )
}

@Composable
@Preview
private fun DottedCircularLoaderPreview() {
    CryptoGraphTheme {
        FullScreenLoader()
    }
}