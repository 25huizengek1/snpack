package me.huizengek.snpack.screens

import android.text.format.Formatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.ramcosta.composedestinations.navigation.navigate
import me.huizengek.snpack.LocalNavigator
import me.huizengek.snpack.stickers.StickerRepository
import me.huizengek.snpack.destinations.CreateNewPackScreenDestination
import me.huizengek.snpack.destinations.SettingsScreenDestination
import me.huizengek.snpack.destinations.StickerPackScreenDestination
import me.huizengek.snpack.ui.components.AppBarAction
import me.huizengek.snpack.ui.components.TopAppBarTitle
import me.huizengek.snpack.util.resolveStickerImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun HomeScreen() {
    val navigator = LocalNavigator.current
    val context = LocalContext.current

    val packs by StickerRepository.packs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TopAppBarTitle(title = "snpack") },
                actions = {
                    AppBarAction(
                        onClick = { navigator.navigate(SettingsScreenDestination) },
                        icon = Icons.Filled.Settings,
                        contentDescription = "Instellingen" // for testing
                    )
                }
            )
        }, floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navigator.navigate(CreateNewPackScreenDestination) },
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
                text = { Text(text = "Nieuw stickerpakket") },
                modifier = Modifier.testTag("homeFab")
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(contentPadding = PaddingValues(bottom = 88.dp)) {
                items(packs) {
                    ListItem(
                        leadingContent = {
                            GlideImage(
                                model = context.resolveStickerImage(it.pack.trayImageFile),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .padding(12.dp)
                            )
                        },
                        headlineContent = { Text(text = it.pack.name) },
                        supportingContent = { Text(text = "${it.stickers.size} stickers") },
                        trailingContent = {
                            Text(text = Formatter.formatShortFileSize(context, it.totalSize))
                        },
                        modifier = Modifier.clickable {
                            navigator.navigate(
                                StickerPackScreenDestination(packId = it.pack.id)
                            )
                        }
                    )
                }
            }
        }
    }
}