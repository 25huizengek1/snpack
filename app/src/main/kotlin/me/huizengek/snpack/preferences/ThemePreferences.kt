package me.huizengek.snpack.preferences

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.huizengek.snpack.GlobalPreferencesHolder
import me.huizengek.snpack.R

object ThemePreferences : GlobalPreferencesHolder() {
    var theme by enum(Theme.SYSTEM)
    var isDynamic by boolean(false)

    enum class Theme(val displayName: @Composable () -> String) {
        SYSTEM(displayName = { stringResource(R.string.theme_system) }),
        LIGHT(displayName = { stringResource(R.string.theme_light) }),
        DARK(displayName = { stringResource(R.string.theme_dark) })
    }
}
