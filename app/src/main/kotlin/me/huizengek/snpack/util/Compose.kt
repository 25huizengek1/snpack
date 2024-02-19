package me.huizengek.snpack.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController

fun Dp.toPx(density: Density) = with(density) { Px(roundToPx()) }

@Composable
fun Dp.toPx() = toPx(LocalDensity.current)

val Int.px get() = Px(this)

@JvmInline
value class Px(val value: Int) {
    fun toDp(density: Density) = with(density) { value.toDp() }

    @Composable
    fun toDp() = toDp(LocalDensity.current)
}

val NavController.canGoBack
    get() = currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED

fun NavController.safeBack() = if (canGoBack) popBackStack() else false
