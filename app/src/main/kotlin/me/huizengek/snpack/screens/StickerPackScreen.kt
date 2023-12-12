package me.huizengek.snpack.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch
import me.huizengek.snpack.Database
import me.huizengek.snpack.LocalNavigator
import me.huizengek.snpack.R
import me.huizengek.snpack.stickers.StickerRepository
import me.huizengek.snpack.stickers.whatsapp.addToWhatsapp
import me.huizengek.snpack.destinations.CreateNewStickerScreenDestination
import me.huizengek.snpack.destinations.EditPackScreenDestination
import me.huizengek.snpack.destinations.StickerScreenDestination
import me.huizengek.snpack.util.findActivity
import me.huizengek.snpack.util.resolveStickerImage
import me.huizengek.snpack.ui.components.AppBarAction
import me.huizengek.snpack.ui.components.ConfirmationDialog
import me.huizengek.snpack.ui.components.NavigationAwareBack
import me.huizengek.snpack.ui.components.TopAppBarTitle

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class,
    ExperimentalFoundationApi::class
)
@Destination
@Composable
fun StickerPackScreen(packId: Long) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    val pack by Database.pack(packId).collectAsState(initial = null)

    var selecting by remember { mutableStateOf(false) }
    val selected = remember { mutableStateListOf<Long>() }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    pack?.let { actualPack ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        TopAppBarTitle(
                            title = if (selecting) stringResource(
                                R.string.format_selected_count,
                                selected.size
                            ) else actualPack.pack.name
                        )
                    },
                    actions = {
                        var opened by remember { mutableStateOf(false) }
                        if (opened) ConfirmationDialog(
                            title = stringResource(R.string.delete_confirmation_title),
                            question = if (selecting) pluralStringResource(
                                id = R.plurals.delete_confirmation_description,
                                count = selected.size
                            ) else stringResource(R.string.delete_all_confirmation_description),
                            confirmButton = stringResource(R.string.delete),
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null
                                )
                            },
                            onDismiss = { confirmed ->
                                if (confirmed) with(context) {
                                    if (selecting) {
                                        val selectedStickers =
                                            actualPack.stickers.filter { it.id in selected }
                                        selected.clear()
                                        selecting = false
                                        selectedStickers.forEach {
                                            StickerRepository.deleteSticker(it)
                                        }
                                    } else {
                                        StickerRepository.deletePack(actualPack)
                                        navigator.popBackStack()
                                    }
                                }
                                opened = false
                            }
                        )

                        AppBarAction(
                            onClick = { opened = true },
                            icon = Icons.Filled.Delete
                        )

                        if (actualPack.stickers.isNotEmpty()) AppBarAction(
                            onClick = {
                                if (actualPack.stickers.size !in (3..30))
                                    return@AppBarAction coroutineScope.launch {
                                        snackbarHostState
                                            .showSnackbar(context.getString(R.string.error_pack_out_of_bounds))
                                    }.let {}
                                context.findActivity()?.apply {
                                    actualPack.pack.addToWhatsapp()
                                }
                            },
                            painter = painterResource(id = R.drawable.upload)
                        )

                        AppBarAction(
                            icon = Icons.Filled.Info,
                            onClick = { navigator.navigate(EditPackScreenDestination(packId = packId)) }
                        )
                    },
                    navigationIcon = {
                        NavigationAwareBack(onClick = { next ->
                            if (selecting) {
                                selected.clear()
                                selecting = false
                            } else next()
                        })
                    }
                )
            },
            floatingActionButton = {
                if (actualPack.stickers.size < 30) ExtendedFloatingActionButton(
                    onClick = {
                        navigator.navigate(CreateNewStickerScreenDestination(packId = actualPack.pack.id))
                    },
                    icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
                    text = { Text(text = stringResource(R.string.new_sticker)) }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(actualPack.stickers) {
                    GlideImage(
                        model = context.resolveStickerImage(it.imageFileName),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .combinedClickable(
                                onLongClick = {
                                    selecting = true
                                    if (it.id !in selected) selected += it.id
                                },
                                onClick = {
                                    if (selecting) {
                                        if (it.id in selected) selected -= it.id else selected += it.id
                                        if (selected.isEmpty()) selecting = false
                                    } else
                                        navigator.navigate(StickerScreenDestination(stickerId = it.id))
                                }
                            )
                    )
                    if (it.id in selected) Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}