package me.huizengek.snpack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import me.huizengek.snpack.R

private fun Color.toHsv(): HsvColor {
    val out = FloatArray(3)
    val color = value.toInt()
    val r = color shr 16 and 0xFF
    val g = color shr 8 and 0xFF
    val b = color and 0xFF

    android.graphics.Color.RGBToHSV(r, g, b, out)
    val (h, s, v) = out
    return HsvColor(h, s, v, alpha)
}

@Composable
fun ColorPickerDialog(
    text: String? = null,
    initialColor: Color,
    setColor: (Color) -> Unit,
    onDismiss: () -> Unit
) = AlertDialog(
    onDismissRequest = { onDismiss() },
    icon = { Icon(imageVector = Icons.Filled.Edit, contentDescription = null) },
    title = { Text(text = stringResource(R.string.color_picker)) },
    text = {
        Column {
            text?.let { Text(text = it) }
            Spacer(modifier = Modifier.height(4.dp))
            ClassicColorPicker(
                color = initialColor.toHsv(),
                onColorChanged = { setColor(it.toColor()) }
            )
        }
    },
    confirmButton = {
        Button(onClick = { onDismiss() }) {
            Text(text = stringResource(R.string.ok_button))
        }
    }
)

@Composable
fun ColorSelector(
    name: String,
    color: Color,
    setColor: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var opened by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = {
            Text(text = name)
        },
        trailingContent = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color, CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
                    .then(modifier)
            )
        },
        modifier = Modifier.clickable { opened = true }
    )

    if (opened) ColorPickerDialog(initialColor = color, setColor = setColor) {
        opened = false
    }
}