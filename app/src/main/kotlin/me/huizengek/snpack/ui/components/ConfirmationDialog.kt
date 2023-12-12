package me.huizengek.snpack.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import me.huizengek.snpack.R

@Composable
fun ConfirmationDialog(
    title: String,
    question: String,
    modifier: Modifier = Modifier,
    confirmButton: String = stringResource(R.string.ok_button),
    dismissButton: String = stringResource(R.string.cancel),
    icon: (@Composable () -> Unit)? = null,
    onDismiss: (Boolean) -> Unit
) = AlertDialog(
    onDismissRequest = { onDismiss(false) },
    title = { Text(text = title) },
    text = { Text(text = question) },
    icon = icon,
    confirmButton = {
        TextButton(onClick = { onDismiss(true) }) {
            Text(text = confirmButton)
        }
    },
    dismissButton = {
        TextButton(onClick = { onDismiss(false) }) {
            Text(text = dismissButton)
        }
    },
    modifier = modifier
)
