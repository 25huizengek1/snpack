package me.huizengek.snpack.preferences

import me.huizengek.snpack.GlobalPreferencesHolder

object ThemePreferences : GlobalPreferencesHolder() {
    var theme by enum(Theme.SYSTEM)
    var isDynamic by boolean(false)

    enum class Theme(val displayName: String) {
        SYSTEM(displayName = "Systeem"),
        LIGHT(displayName = "Licht"),
        DARK(displayName = "Donker")
    }
}