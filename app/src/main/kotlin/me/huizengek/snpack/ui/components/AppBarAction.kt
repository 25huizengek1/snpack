package me.huizengek.snpack.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AppBarAction(icon: ImageVector, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(imageVector = icon, contentDescription = null)
    }
}
@Composable
fun AppBarAction(painter: Painter, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(painter = painter, contentDescription = null)
    }
}