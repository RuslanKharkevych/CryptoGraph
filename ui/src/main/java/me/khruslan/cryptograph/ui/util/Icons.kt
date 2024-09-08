@file:Suppress("ObjectPropertyName", "UnusedReceiverParameter")

package me.khruslan.cryptograph.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

private var _currencyBitcoin: ImageVector? = null
private var _starOutline: ImageVector? = null
private var _trendingDown: ImageVector? = null
private var _trendingFlat: ImageVector? = null
private var _trendingUp: ImageVector? = null

internal val Icons.Filled.CurrencyBitcoin: ImageVector
    get() {
        if (_currencyBitcoin != null) {
            return _currencyBitcoin!!
        }
        _currencyBitcoin = materialIcon(name = "Filled.CurrencyBitcoin") {
            materialPath {
                moveTo(17.06f, 11.57f)
                curveTo(17.65f, 10.88f, 18.0f, 9.98f, 18.0f, 9.0f)
                curveToRelative(0.0f, -1.86f, -1.27f, -3.43f, -3.0f, -3.87f)
                lineTo(15.0f, 3.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineTo(3.0f)
                horizontalLineTo(9.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(6.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(10.0f)
                horizontalLineTo(6.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(3.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                curveToRelative(2.21f, 0.0f, 4.0f, -1.79f, 4.0f, -4.0f)
                curveTo(19.0f, 13.55f, 18.22f, 12.27f, 17.06f, 11.57f)
                close()
                moveTo(10.0f, 7.0f)
                horizontalLineToRelative(4.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
                reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
                horizontalLineToRelative(-4.0f)
                verticalLineTo(7.0f)
                close()
                moveTo(15.0f, 17.0f)
                horizontalLineToRelative(-5.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineToRelative(5.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
                reflectiveCurveTo(16.1f, 17.0f, 15.0f, 17.0f)
                close()
            }
        }
        return _currencyBitcoin!!
    }

internal val Icons.Filled.StarOutline: ImageVector
    get() {
        if (_starOutline != null) {
            return _starOutline!!
        }
        _starOutline = materialIcon(name = "Filled.StarOutline") {
            materialPath {
                moveTo(22.0f, 9.24f)
                lineToRelative(-7.19f, -0.62f)
                lineTo(12.0f, 2.0f)
                lineTo(9.19f, 8.63f)
                lineTo(2.0f, 9.24f)
                lineToRelative(5.46f, 4.73f)
                lineTo(5.82f, 21.0f)
                lineTo(12.0f, 17.27f)
                lineTo(18.18f, 21.0f)
                lineToRelative(-1.63f, -7.03f)
                lineTo(22.0f, 9.24f)
                close()
                moveTo(12.0f, 15.4f)
                lineToRelative(-3.76f, 2.27f)
                lineToRelative(1.0f, -4.28f)
                lineToRelative(-3.32f, -2.88f)
                lineToRelative(4.38f, -0.38f)
                lineTo(12.0f, 6.1f)
                lineToRelative(1.71f, 4.04f)
                lineToRelative(4.38f, 0.38f)
                lineToRelative(-3.32f, 2.88f)
                lineToRelative(1.0f, 4.28f)
                lineTo(12.0f, 15.4f)
                close()
            }
        }
        return _starOutline!!
    }

internal val Icons.AutoMirrored.Filled.TrendingDown: ImageVector
    get() {
        if (_trendingDown != null) {
            return _trendingDown!!
        }
        _trendingDown = materialIcon(name = "AutoMirrored.Filled.TrendingDown", autoMirror = true) {
            materialPath {
                moveTo(16.0f, 18.0f)
                lineToRelative(2.29f, -2.29f)
                lineToRelative(-4.88f, -4.88f)
                lineToRelative(-4.0f, 4.0f)
                lineTo(2.0f, 7.41f)
                lineTo(3.41f, 6.0f)
                lineToRelative(6.0f, 6.0f)
                lineToRelative(4.0f, -4.0f)
                lineToRelative(6.3f, 6.29f)
                lineTo(22.0f, 12.0f)
                verticalLineToRelative(6.0f)
                close()
            }
        }
        return _trendingDown!!
    }

internal val Icons.AutoMirrored.Filled.TrendingFlat: ImageVector
    get() {
        if (_trendingFlat != null) {
            return _trendingFlat!!
        }
        _trendingFlat = materialIcon(name = "AutoMirrored.Filled.TrendingFlat", autoMirror = true) {
            materialPath {
                moveTo(22.0f, 12.0f)
                lineToRelative(-4.0f, -4.0f)
                verticalLineToRelative(3.0f)
                horizontalLineTo(3.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(15.0f)
                verticalLineToRelative(3.0f)
                close()
            }
        }
        return _trendingFlat!!
    }

internal val Icons.AutoMirrored.Filled.TrendingUp: ImageVector
    get() {
        if (_trendingUp != null) {
            return _trendingUp!!
        }
        _trendingUp = materialIcon(name = "AutoMirrored.Filled.TrendingUp", autoMirror = true) {
            materialPath {
                moveTo(16.0f, 6.0f)
                lineToRelative(2.29f, 2.29f)
                lineToRelative(-4.88f, 4.88f)
                lineToRelative(-4.0f, -4.0f)
                lineTo(2.0f, 16.59f)
                lineTo(3.41f, 18.0f)
                lineToRelative(6.0f, -6.0f)
                lineToRelative(4.0f, 4.0f)
                lineToRelative(6.3f, -6.29f)
                lineTo(22.0f, 12.0f)
                verticalLineTo(6.0f)
                close()
            }
        }
        return _trendingUp!!
    }