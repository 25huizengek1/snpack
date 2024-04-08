package me.huizengek.snpack.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import me.huizengek.snpack.R
import me.huizengek.snpack.preferences.ThemePreferences
import me.huizengek.snpack.stickers.BackupRepository
import me.huizengek.snpack.stickers.BackupRepository.backup
import me.huizengek.snpack.stickers.BackupRepository.restore
import me.huizengek.snpack.ui.components.EnumSelectorSettingsEntry
import me.huizengek.snpack.ui.components.NavigationAwareBack
import me.huizengek.snpack.ui.components.SettingsEntry
import me.huizengek.snpack.ui.components.SettingsGroup
import me.huizengek.snpack.ui.components.SwitchSettingsEntry
import me.huizengek.snpack.ui.components.TopAppBarTitle
import me.huizengek.snpack.util.isAtLeastAndroid12

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun SettingsScreen() = Scaffold(
    topBar = {
        TopAppBar(
            title = { TopAppBarTitle(title = stringResource(R.string.settings)) },
            navigationIcon = { NavigationAwareBack() }
        )
    }
) { paddingValues ->
    val context = LocalContext.current

    val createDocument = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(BackupRepository.MIME_TYPE)
    ) {
        if (it == null) return@rememberLauncherForActivityResult

        BackupRepository.obtain {
            with(context) {
                backup(it, "snpack autobackup")
            }
        }
    }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        if (it == null) return@rememberLauncherForActivityResult

        BackupRepository.obtain {
            with(context) {
                restore(it)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        SettingsGroup(title = stringResource(R.string.appearance)) {
            EnumSelectorSettingsEntry(
                title = stringResource(R.string.theme),
                selectedValue = ThemePreferences.theme,
                onValueSelected = { ThemePreferences.theme = it },
                valueDisplayText = { it.displayName() }
            )
            SwitchSettingsEntry(
                title = stringResource(R.string.dynamic_theme),
                description = stringResource(R.string.dynamic_theme_description),
                state = ThemePreferences.isDynamic,
                setState = { ThemePreferences.isDynamic = it },
                enabled = isAtLeastAndroid12
            )
        }
        SettingsGroup(title = stringResource(R.string.backup)) {
            SettingsEntry(
                title = stringResource(R.string.backup_create),
                description = stringResource(R.string.backup_create_description),
                onClick = {
                    createDocument.launch("snpack-${System.currentTimeMillis()}.zip")
                }
            )
            SettingsEntry(
                title = stringResource(R.string.backup_restore),
                description = stringResource(R.string.backup_restore_description),
                onClick = {
                    picker.launch(arrayOf(BackupRepository.MIME_TYPE))
                }
            )
        }
    }
}
