package me.khruslan.cryptograph.ui.util

import android.content.Context
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontListFontFamily
import androidx.compose.ui.text.font.ResourceFont
import androidx.core.content.res.ResourcesCompat

private val cachedTypefaces = mutableMapOf<Int, Typeface>()

internal val TextStyle.typeface
    @Composable
    get() = runCatching { resolveTypeface(LocalContext.current) }.getOrNull()

private fun TextStyle.resolveTypeface(context: Context): Typeface {
    val fontListFamily = fontFamily as FontListFontFamily
    val font = fontListFamily.fonts.first { it.weight == fontWeight }
    val resId = (font as ResourceFont).resId
    cachedTypefaces[resId]?.let { return it }
    return ResourcesCompat.getFont(context, resId)!!.also { cachedTypefaces[resId] = it }
}