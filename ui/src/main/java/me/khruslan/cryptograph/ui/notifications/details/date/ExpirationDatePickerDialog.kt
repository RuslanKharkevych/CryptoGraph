package me.khruslan.cryptograph.ui.notifications.details.date

import android.content.res.Configuration
import android.os.Build
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.core.CryptoGraphTheme
import me.khruslan.cryptograph.ui.util.getCurrentLocale
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExpirationDatePickerDialog(
    state: DatePickerState,
    onDateSelected: (date: LocalDate?) -> Unit,
    onDismiss: () -> Unit,
) {
    val initialDisplayMode = remember(state) {
        state.displayMode
    }

    fun confirmDate() {
        val date = state.selectedDateMillis?.let { epochMillis ->
            localDateFromEpochMillis(epochMillis)
        }
        onDateSelected(date)
        onDismiss()
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = ::confirmDate) {
                Text(text = stringResource(R.string.date_picker_positive_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.date_picker_negative_btn))
            }
        },
        content = {
            LocaleProvider {
                DatePicker(
                    state = state,
                    showModeToggle = initialDisplayMode == DisplayMode.Picker
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun rememberExpirationDatePickerState(
    initialDate: LocalDate?,
    initialDisplayMode: DisplayMode,
): DatePickerState {
    return rememberDatePickerState(
        initialSelectedDateMillis = initialDate?.toEpochMillis(),
        initialDisplayMode = initialDisplayMode,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= LocalDate.now().toEpochMillis()
            }
        }
    )
}

@Composable
private fun LocaleProvider(content: @Composable () -> Unit) {
    val locale = getCurrentLocale()

    CompositionLocalProvider(
        value = LocalConfiguration provides LocalConfiguration.current.withLocale(locale),
        content = content
    )
}

private fun Configuration.withLocale(locale: Locale): Configuration {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        setLocale(locale)
    } else {
        @Suppress("deprecation")
        this.locale = locale
    }

    setLayoutDirection(locale)
    return this
}

private fun LocalDate.toEpochMillis(): Long {
    return atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}

private fun localDateFromEpochMillis(epochMillis: Long): LocalDate {
    val instant = Instant.ofEpochMilli(epochMillis)
    return instant.atZone(ZoneOffset.UTC).toLocalDate()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@PreviewLightDark
private fun ExpirationDatePickerDialogPreview() {
    CryptoGraphTheme {
        ExpirationDatePickerDialog(
            state = rememberDatePickerState(),
            onDateSelected = {},
            onDismiss = {}
        )
    }
}