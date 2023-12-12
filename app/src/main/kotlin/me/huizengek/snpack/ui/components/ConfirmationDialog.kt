package me.huizengek.snpack.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.huizengek.snpack.R

@Composable
fun ConfirmationDialog(
    title: String,
    question: String,
    confirmButton: String = stringResource(R.string.ok_button),
    dismissButton: String = stringResource(R.string.cancel),
    onDismiss: (Boolean) -> Unit,
    icon: (@Composable () -> Unit)? = null
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
    }
)