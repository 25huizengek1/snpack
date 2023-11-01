package me.huizengek.snpack.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
inline fun <T> ValueSelectorDialog(
    modifier: Modifier = Modifier,
    noinline onDismiss: () -> Unit,
    title: String,
    selectedValue: T,
    values: List<T>,
    crossinline onValueSelected: (T) -> Unit,
    crossinline valueDisplayText: (T) -> String = { it.toString() },
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
) = Dialog(onDismissRequest = onDismiss) {
    Surface(
        shape = shape,
        color = containerColor,
        tonalElevation = tonalElevation,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(vertical = 24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                values.forEach { value ->
                    ListItem(
                        headlineContent = { Text(text = valueDisplayText(value)) },
                        leadingContent = {
                            RadioButton(
                                selected = selectedValue == value,
                                onClick = {
                                    onValueSelected(value)
                                    onDismiss()
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onValueSelected(value)
                                onDismiss()
                            }
                    )
                }
            }
        }
    }
}