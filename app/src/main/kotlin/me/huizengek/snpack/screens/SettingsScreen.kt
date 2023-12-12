package me.huizengek.snpack.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import me.huizengek.snpack.R
import me.huizengek.snpack.preferences.ThemePreferences
import me.huizengek.snpack.ui.components.EnumSelectorSettingsEntry
import me.huizengek.snpack.ui.components.NavigationAwareBack
import me.huizengek.snpack.ui.components.SettingsGroupSpacer
import me.huizengek.snpack.ui.components.SettingsGroupText
import me.huizengek.snpack.ui.components.SwitchSettingsEntry
import me.huizengek.snpack.ui.components.TopAppBarTitle
import me.huizengek.snpack.util.isAtLeastAndroid12

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun SettingsScreen() = Scaffold(topBar = {
    TopAppBar(
        title = { TopAppBarTitle(title = stringResource(R.string.settings)) },
        navigationIcon = { NavigationAwareBack() }
    )
}) { paddingValues ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        SettingsGroupText(title = stringResource(R.string.appearance))
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
        SettingsGroupSpacer()
    }
}
