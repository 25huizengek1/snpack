package me.huizengek.snpack.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AppBarAction(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onClick: () -> Unit
) = IconButton(
    onClick = onClick,
    modifier = modifier
) {
    Icon(imageVector = icon, contentDescription = contentDescription)
}

@Composable
fun AppBarAction(
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onClick: () -> Unit
) = IconButton(
    onClick = onClick,
    modifier = modifier
) {
    Icon(painter = painter, contentDescription = contentDescription)
}
