package me.huizengek.snpack.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPx() = with(LocalDensity.current) { toPx() }

val Int.px get() = Px(this)

@JvmInline
value class Px(val value: Int)

@Composable
fun Px.toDp() = with(LocalDensity.current) { value.toDp() }