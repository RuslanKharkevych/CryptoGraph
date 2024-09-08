package me.khruslan.cryptograph.ui.util

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry

internal fun route(path: String, arguments: List<NamedNavArgument>): String {
    return buildString {
        append(path)
        arguments.forEachIndexed { index, argument ->
            append(if (index == 0) '?' else '&')
            append("${argument.name}={${argument.name}}")
        }
    }
}

internal fun route(path: String, builderScope: RouteBuilder.() -> Unit): String {
    return RouteBuilder(path).apply(builderScope).build()
}

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

@Composable
internal fun rememberNavInterceptor(navBackStackEntry: NavBackStackEntry): NavInterceptor {
    return remember(navBackStackEntry) {
        NavInterceptor(navBackStackEntry)
    }
}

internal class NavInterceptor(private val navBackStackEntry: NavBackStackEntry) {

    private val lifecycleResumed
        get() = navBackStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED

    operator fun invoke(action: () -> Unit): () -> Unit = {
        if (lifecycleResumed) action()
    }

    operator fun <T> invoke(action: (T) -> Unit): (T) -> Unit = { arg1 ->
        if (lifecycleResumed) action(arg1)
    }
}

internal typealias TransitionScope = AnimatedContentTransitionScope<NavBackStackEntry>

internal object Transitions {

    object Enter {
        fun slideLtr(scope: TransitionScope): EnterTransition {
            return scope.slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        }

        fun slideRtl(scope: TransitionScope): EnterTransition {
            return scope.slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        }
    }

    object Exit {
        fun slideLtr(scope: TransitionScope): ExitTransition {
            return scope.slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        }

        fun slideRtl(scope: TransitionScope): ExitTransition {
            return scope.slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        }
    }
}