package me.huizengek.snpack.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController

@Composable
fun Dp.toPx() = with(LocalDensity.current) { toPx() }

val Int.px get() = Px(this)

@JvmInline
value class Px(val value: Int)

@Composable
fun Px.toDp() = with(LocalDensity.current) { value.toDp() }

val NavController.canGoBack
    get() = currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED

fun NavController.safeBack() = if (canGoBack) popBackStack() else false
