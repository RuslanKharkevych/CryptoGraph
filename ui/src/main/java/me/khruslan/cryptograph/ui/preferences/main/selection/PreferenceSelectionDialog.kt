package me.khruslan.cryptograph.ui.preferences.main.selection

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.preferences.Theme
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.util.ChoiceItem
import me.khruslan.cryptograph.ui.util.ChoiceItems

private const val LOG_TAG = "PreferenceSelectionDialog"

@Composable
internal fun <T> PreferenceSelectionDialog(
    title: String,
    items: List<ChoiceItem<T>>,
    selectedItem: T,
    onItemSelected: (item: T) -> Unit,
    onDismiss: () -> Unit,
) {
    fun selectItemAndDismiss(item: T) {
        Logger.info(LOG_TAG, "Item selected: $item")
        onItemSelected(item)
        onDismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        BoxWithConstraints(contentAlignment = Alignment.Center) {
            PreferenceSelectionDialogContent(
                modifier = Modifier.heightIn(max = maxHeight * 0.9f),
                title = title,
                items = items,
                selectedItem = selectedItem,
                onItemSelected = ::selectItemAndDismiss,
                onDismiss = onDismiss
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> PreferenceSelectionDialogContent(
    modifier: Modifier,
    title: String,
    items: List<ChoiceItem<T>>,
    selectedItem: T,
    onItemSelected: (item: T) -> Unit,
    onDismiss: () -> Unit,
) {
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
    ) {
        TopBar(
            title = title,
            scrollBehavior = topBarScrollBehavior,
            onDismiss = onDismiss
        )
        ChoiceItemsList(
            items = items,
            selectedItem = selectedItem,
            onItemSelected = onItemSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onDismiss: () -> Unit,
) {
    val shadow by animateDpAsState(
        label = "SelectionDialogTopBarShadowDpAnimation",
        targetValue = 8.dp * scrollBehavior.state.overlappedFraction
    )

    TopAppBar(
        modifier = Modifier.shadow(shadow),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        },
        actions = {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.close_action_desc)
                )
            }
        }
    )
}

@Composable
private fun <T> ChoiceItemsList(
    items: List<ChoiceItem<T>>,
    selectedItem: T,
    onItemSelected: (item: T) -> Unit,
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        items.forEach { (item, labelRes) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = item == selectedItem,
                        onClick = { onItemSelected(item) }
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = item == selectedItem,
                    onClick = { onItemSelected(item) }
                )
                Text(
                    text = stringResource(labelRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
@PreviewLightDark
private fun PreferenceSelectionDialogPreview() {
    CryptoGraphTheme {
        PreferenceSelectionDialogContent(
            modifier = Modifier,
            title = stringResource(R.string.select_theme_dialog_title),
            items = ChoiceItems.Themes,
            selectedItem = Theme.Auto,
            onItemSelected = {},
            onDismiss = {}
        )
    }
}