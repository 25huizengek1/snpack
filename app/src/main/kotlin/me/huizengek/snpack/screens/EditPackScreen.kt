package me.huizengek.snpack.screens

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.ramcosta.composedestinations.annotation.Destination
import me.huizengek.snpack.Database
import me.huizengek.snpack.LocalNavigator
import me.huizengek.snpack.R
import me.huizengek.snpack.stickers.StickerRepository
import me.huizengek.snpack.util.resolveStickerImage
import me.huizengek.snpack.util.safeBack
import me.huizengek.snpack.util.uriSaver
import me.huizengek.snpack.util.useAsInput

@Destination
@Composable
fun EditPackScreen(packId: Long) {
    val context = LocalContext.current
    val pack by Database.pack(packId).collectAsState(initial = null)

    pack?.let { actualPack ->
        val navigator = LocalNavigator.current

        var name by rememberSaveable { mutableStateOf(actualPack.pack.name) }
        var publisher by rememberSaveable { mutableStateOf(actualPack.pack.publisher) }
        var imageUri by rememberSaveable(stateSaver = uriSaver) {
            mutableStateOf(
                context.resolveStickerImage(
                    actualPack.pack.trayImageFile
                ).toUri()
            )
        }

        PackEditorScreen(
            title = stringResource(R.string.format_edit, actualPack.pack.name),
            name = name,
            setName = { name = it },
            publisher = publisher,
            setPublisher = { publisher = it },
            imageUri = imageUri,
            setImageUri = { imageUri = it },
            onSave = {
                imageUri?.useAsInput("image/*") {
                    StickerRepository.updatePack(
                        packId,
                        name,
                        publisher,
                        BitmapFactory.decodeStream(it)
                    )
                    navigator.safeBack()
                }
            }
        )
    }
}
