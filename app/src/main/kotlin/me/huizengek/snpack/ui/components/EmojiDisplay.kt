package me.huizengek.snpack.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmojiDisplay(
    index: Int,
    setIndex: (Int) -> Unit,
    emojis: SnapshotStateMap<Int, String>,
    modifier: Modifier = Modifier,
    useHaptics: Boolean = true
) = Row(
    horizontalArrangement = Arrangement.spacedBy(
        space = 8.dp,
        alignment = Alignment.CenterHorizontally
    ),
    modifier = modifier.fillMaxWidth()
) {
    val haptics = LocalHapticFeedback.current

    for (i in 0..2) {
        val animatedCornerRadius by animateDpAsState(
            targetValue = if (index == i) 12.dp else 8.dp,
            label = ""
        )
        val animatedBackground by animateColorAsState(
            targetValue = if (index == i) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.onSurface,
            label = ""
        )

        val interactionSource = remember { MutableInteractionSource() }

        Surface(
            color = animatedBackground,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
            shape = RoundedCornerShape(animatedCornerRadius),
            modifier = Modifier
                .size(64.dp)
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { setIndex(i) },
                    onDoubleClick = {
                        emojis.remove(i)
                        if (useHaptics) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                )
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = emojis[i].orEmpty(), fontSize = 32.sp)
            }
        }
    }
}
