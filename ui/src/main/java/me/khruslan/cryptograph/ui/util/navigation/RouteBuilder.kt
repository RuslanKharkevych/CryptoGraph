package me.khruslan.cryptograph.ui.util.navigation

import android.net.Uri
import androidx.navigation.NamedNavArgument

internal class RouteBuilder(route: String) {
    private var stringBuilder = StringBuilder(route)

    fun argument(key: String, value: Any?) {
        if (value != null) {
            stringBuilder
                .append(if (stringBuilder.contains('?')) '&' else '?')
                .append("$key=${Uri.encode(value.toString())}")
        }
    }

    fun build() = stringBuilder.toString()
}

internal fun route(path: String, builderScope: RouteBuilder.() -> Unit): String {
    return RouteBuilder(path).apply(builderScope).build()
}

internal fun route(path: String, arguments: List<NamedNavArgument>): String {
    return buildString {
        append(path)
        arguments.forEachIndexed { index, argument ->
            append(if (index == 0) '?' else '&')
            append("${argument.name}={${argument.name}}")
        }
    }
}