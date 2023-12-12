package me.huizengek.snpack.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

@Composable
fun SettingsGroupText(
    modifier: Modifier = Modifier,
    title: String
) = Text(
    text = title.uppercase(),
    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary),
    modifier = modifier
        .padding(start = 16.dp)
        .padding(horizontal = 16.dp)
)

@Composable
fun SettingsGroupSpacer(modifier: Modifier = Modifier) = Spacer(modifier = modifier.height(24.dp))

@Composable
fun SettingsEntry(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val animatedAlpha by animateFloatAsState(targetValue = if (enabled) 1f else 0.5f, label = "")

    ListItem(
        headlineContent = { Text(text = title) },
        supportingContent = description?.let { { Text(text = it) } },
        trailingContent = trailingContent,
        leadingContent = { },
        modifier = modifier
            .fillMaxWidth()
            .alpha(animatedAlpha)
            .clickable { onClick?.let { it() } }
    )
}

@Composable
fun SwitchSettingsEntry(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    state: Boolean,
    setState: (Boolean) -> Unit,
    enabled: Boolean = true
) = SettingsEntry(
    modifier = modifier,
    title = title,
    description = description,
    onClick = { setState(!state) },
    enabled = enabled,
    trailingContent = {
        BoxWithConstraints {
            Switch(
                checked = state,
                onCheckedChange = { setState(it) },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
)

@Composable
inline fun <T> ValueSelectorSettingsEntry(
    modifier: Modifier = Modifier,
    title: String,
    selectedValue: T,
    values: List<T>,
    crossinline onValueSelected: (T) -> Unit,
    enabled: Boolean = true,
    crossinline valueDisplayText: @Composable (T) -> String = { it.toString() },
    noinline trailingContent: (@Composable () -> Unit)? = null
) {
    var open by remember { mutableStateOf(false) }

    if (open) ValueSelectorDialog(
        onDismiss = { open = false },
        title = title,
        selectedValue = selectedValue,
        values = values,
        onValueSelected = onValueSelected,
        valueDisplayText = valueDisplayText
    )

    SettingsEntry(
        modifier = modifier,
        title = title,
        description = valueDisplayText(selectedValue),
        onClick = { open = true },
        enabled = enabled,
        trailingContent = trailingContent
    )
}

@Composable
inline fun <reified T : Enum<T>> EnumSelectorSettingsEntry(
    modifier: Modifier = Modifier,
    title: String,
    selectedValue: T,
    crossinline onValueSelected: (T) -> Unit,
    enabled: Boolean = true,
    crossinline valueDisplayText: @Composable (T) -> String = { it.name },
    noinline trailingContent: (@Composable () -> Unit)? = null
) = ValueSelectorSettingsEntry(
    modifier = modifier,
    title = title,
    selectedValue = selectedValue,
    values = enumValues<T>().toList(),
    onValueSelected = onValueSelected,
    enabled = enabled,
    valueDisplayText = valueDisplayText,
    trailingContent = trailingContent
)