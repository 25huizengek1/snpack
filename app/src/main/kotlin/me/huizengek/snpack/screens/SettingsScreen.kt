package me.huizengek.snpack.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination
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
        title = { TopAppBarTitle(title = "Instellingen") },
        navigationIcon = { NavigationAwareBack() }
    )
}) { paddingValues ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        SettingsGroupText(title = "Uiterlijk")
        EnumSelectorSettingsEntry(
            title = "Thema",
            selectedValue = ThemePreferences.theme ?: ThemePreferences.Theme.SYSTEM,
            onValueSelected = { ThemePreferences.theme = it },
            valueDisplayText = { it.displayName }
        )
        SwitchSettingsEntry(
            title = "Dynamisch thema",
            description = "Maakt het thema van de app dynamisch a.d.h.v. je achtergrond. Vereist Android 12 of hoger.",
            state = ThemePreferences.isDynamic,
            setState = { ThemePreferences.isDynamic = it },
            enabled = isAtLeastAndroid12
        )
        SettingsGroupSpacer()
    }
}