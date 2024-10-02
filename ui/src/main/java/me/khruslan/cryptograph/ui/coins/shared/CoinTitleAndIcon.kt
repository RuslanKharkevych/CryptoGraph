package me.khruslan.cryptograph.ui.coins.shared

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.util.CurrencyBitcoin
import me.khruslan.cryptograph.ui.util.preview.PreviewLightDarkWithBackground
import me.khruslan.cryptograph.ui.util.previewPlaceholder

@Composable
internal fun CoinTitleAndIcon(
    modifier: Modifier = Modifier,
    symbol: String?,
    name: String,
    iconUrl: String?,
) {
    Row(modifier = modifier.basicMarquee()) {
        if (symbol != null) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
        if (iconUrl != null) {
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                model = iconUrl,
                contentDescription = stringResource(R.string.coin_icon_desc, name),
                placeholder = previewPlaceholder(Icons.Default.CurrencyBitcoin)
            )
        }
    }
}

@Composable
@PreviewLightDarkWithBackground
private fun CoinTitleAndIconPreview() {
    CryptoGraphTheme {
        CoinTitleAndIcon(
            modifier = Modifier.padding(4.dp),
            symbol = "BTC",
            name = "Bitcoin",
            iconUrl = "https://cdn.coinranking.com/B1N19L_dZ/bnb.svg"
        )
    }
}