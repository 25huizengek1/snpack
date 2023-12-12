package me.huizengek.snpack.screens

import android.text.format.Formatter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.ramcosta.composedestinations.annotation.Destination
import me.huizengek.snpack.Database
import me.huizengek.snpack.LocalNavigator
import me.huizengek.snpack.R
import me.huizengek.snpack.stickers.StickerRepository
import me.huizengek.snpack.ui.components.AppBarAction
import me.huizengek.snpack.ui.components.ConfirmationDialog
import me.huizengek.snpack.ui.components.NavigationAwareBack
import me.huizengek.snpack.ui.components.TopAppBarTitle
import me.huizengek.snpack.util.resolveStickerImage

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Destination
@Composable
fun StickerScreen(stickerId: Long) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    val sticker by Database.sticker(id = stickerId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TopAppBarTitle(title = "Sticker") },
                navigationIcon = { NavigationAwareBack() },
                actions = {
                    var opened by remember { mutableStateOf(false) }

                    if (opened) ConfirmationDialog(
                        title = stringResource(R.string.delete_confirmation_title),
                        question = pluralStringResource(
                            id = R.plurals.delete_confirmation_description,
                            count = 1
                        ),
                        confirmButton = stringResource(R.string.delete),
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null
                            )
                        },
                        onDismiss = { confirmed ->
                            if (confirmed) with(context) {
                                sticker?.let { StickerRepository.deleteSticker(it) }
                                navigator.popBackStack()
                            }
                            opened = false
                        }
                    )

                    AppBarAction(
                        onClick = { opened = true },
                        icon = Icons.Filled.Delete
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            sticker?.let { actualSticker ->
                GlideImage(
                    model = context.resolveStickerImage(actualSticker.imageFileName),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .aspectRatio(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = Formatter.formatShortFileSize(context, actualSticker.size))
            }
        }
    }
}