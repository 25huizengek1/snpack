package me.huizengek.snpack.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch
import me.huizengek.snpack.LocalNavigator
import me.huizengek.snpack.R
import me.huizengek.snpack.destinations.NavRootDestination
import me.huizengek.snpack.destinations.StickerPackScreenDestination
import me.huizengek.snpack.stickers.StickerRepository
import me.huizengek.snpack.ui.components.NavigationAwareBack
import me.huizengek.snpack.ui.components.TopAppBarTitle
import me.huizengek.snpack.util.uriSaver
import me.huizengek.snpack.util.useAsInput

@Destination
@Composable
fun CreateNewPackScreen() {
    val navigator = LocalNavigator.current

    var name by rememberSaveable { mutableStateOf("") }
    var publisher by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable(stateSaver = uriSaver) { mutableStateOf(null) }

    PackEditorScreen(
        title = stringResource(R.string.new_pack),
        name = name,
        setName = { name = it },
        publisher = publisher,
        setPublisher = { publisher = it },
        imageUri = imageUri,
        setImageUri = { imageUri = it },
        onSave = {
            imageUri?.useAsInput("image/*") {
                val pack = StickerRepository.insertPack(
                    name = name,
                    publisher = publisher,
                    trayImage = BitmapFactory.decodeStream(it)
                )
                if (pack != null) navigator.navigate(StickerPackScreenDestination(packId = pack.id)) {
                    popUpTo(NavRootDestination.route)
                }
            }
        }
    )
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PackEditorScreen(
    title: String,
    name: String,
    setName: (String) -> Unit,
    publisher: String,
    setPublisher: (String) -> Unit,
    imageUri: Uri?,
    setImageUri: (Uri?) -> Unit,
    onSave: suspend Context.() -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { setImageUri(it) }
    )
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TopAppBarTitle(title = title) },
                navigationIcon = { NavigationAwareBack() }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { setName(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                maxLines = 1,
                placeholder = { Text(text = stringResource(R.string.pack_name_placeholder)) },
                label = { Text(text = stringResource(R.string.name)) }
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = publisher,
                onValueChange = { setPublisher(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                maxLines = 1,
                label = { Text(text = stringResource(R.string.publisher)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                ElevatedButton(
                    onClick = {
                        picker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(R.string.choose_icon))
                }
                Button(onClick = {
                    coroutineScope.launch {
                        if (name.isBlank() || publisher.isBlank()) return@launch snackbarHostState
                            .showSnackbar(context.getString(R.string.error_pack_empty_field))
                            .let { }
                        if (imageUri == null) return@launch snackbarHostState
                            .showSnackbar(context.getString(R.string.error_pack_no_image))
                            .let { }

                        with(context) { onSave() }
                    }
                }) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(R.string.save))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            GlideImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .let { if (imageUri == null) it.background(Color.Black.copy(alpha = 0.8f)) else it }
                    .padding(12.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
