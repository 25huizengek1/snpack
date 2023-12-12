package me.huizengek.snpack.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun TopAppBarTitle(
    title: String,
    modifier: Modifier = Modifier
) = Text(
    text = title,
    softWrap = false,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
    modifier = modifier
)
