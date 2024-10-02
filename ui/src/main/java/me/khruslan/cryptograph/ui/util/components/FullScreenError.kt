package me.khruslan.cryptograph.ui.util.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.util.preview.PreviewLightDarkWithBackground

@Composable
internal fun FullScreenError(message: String, onRetryClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedRetryButton(onClick = onRetryClick)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun AnimatedRetryButton(onClick: () -> Unit) {
    val rotationDegrees by animateRotationAngle()

    IconButton(
        modifier = Modifier
            .size(100.dp)
            .rotate(rotationDegrees),
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            imageVector = Icons.Default.Refresh,
            contentDescription = stringResource(R.string.retry_btn_desc),
            tint = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun animateRotationAngle(): State<Float> {
    val currentAngleTransition = rememberInfiniteTransition(
        label = "RetryButtonRotationAngleTransition"
    )

    return currentAngleTransition.animateFloat(
        label = "RetryButtonRotationAngleAnimation",
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                delayMillis = 1000,
                durationMillis = 3000
            )
        )
    )
}

@Composable
@PreviewLightDarkWithBackground
private fun FullScreenErrorPreview() {
    CryptoGraphTheme {
        FullScreenError(
            message = stringResource(R.string.server_error_msg),
            onRetryClick = {}
        )
    }
}