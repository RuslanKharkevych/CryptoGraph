package me.khruslan.cryptograph.ui.util.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val MODAL_SCREEN_MARKER_ARG_KEY = "is-modal-screen"
private typealias TransitionScope = AnimatedContentTransitionScope<NavBackStackEntry>

internal object Transitions {

    val enter: TransitionScope.() -> EnterTransition
        get() = {
            val direction = if (targetState.isModalScreen) {
                SlideDirection.Up
            } else {
                SlideDirection.Left
            }

            slideIntoContainer(
                towards = direction,
                animationSpec = animationSpec
            )
        }

    val exit: TransitionScope.() -> ExitTransition
        get() = { ExitTransition.None }

    val popEnter: TransitionScope.() -> EnterTransition
        get() = { EnterTransition.None }

    val popExit: TransitionScope.() -> ExitTransition
        get() = {
            val direction = if (initialState.isModalScreen) {
                SlideDirection.Down
            } else {
                SlideDirection.Right
            }

            slideOutOfContainer(
                towards = direction,
                animationSpec = animationSpec
            )
        }

    private val NavBackStackEntry.isModalScreen
        get() = destination.arguments.containsKey(MODAL_SCREEN_MARKER_ARG_KEY)

    private val animationSpec
        get() = tween<IntOffset>(durationMillis = 350)
}

internal fun NavGraphBuilder.modal(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    val modalScreenMarkerArg = navArgument(MODAL_SCREEN_MARKER_ARG_KEY) {
        type = NavType.BoolType
        defaultValue = true
    }

    return composable(
        route = route,
        arguments = arguments + modalScreenMarkerArg,
        content = content
    )
}