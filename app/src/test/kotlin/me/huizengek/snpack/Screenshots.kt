package me.huizengek.snpack

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import me.huizengek.snpack.preferences.ThemePreferences
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w400dp-h712dp-normal-notlong-notround-any-560dpi-keyshidden-nonav")
class Screenshots {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private var atomic = 1
        get() = field++

    @Test
    fun testTakeScreenshots() = with(composeTestRule) {
        onNodeWithText("snpack").assertIsDisplayed()

        ThemePreferences.theme = ThemePreferences.Theme.LIGHT
        ThemePreferences.isDynamic = false
        take("light_static")
        ThemePreferences.isDynamic = true
        take("light_dynamic")

        ThemePreferences.theme = ThemePreferences.Theme.DARK
        ThemePreferences.isDynamic = false
        take("dark_static")
        ThemePreferences.isDynamic = true
        take("dark_dynamic")
    }

    @OptIn(ExperimentalTestApi::class)
    private fun take(prefix: String) = with(composeTestRule) {
        createScreenshot("${prefix}_home")

        waitUntilAtLeastOneExists(hasTestTag("homeFab"))
        onNodeWithTag("homeFab").performClick()
        waitUntilAtLeastOneExists(hasText("Opslaan"))
        createScreenshot("${prefix}_new_pack")

        activity.onBackPressedDispatcher.onBackPressed()
        waitUntilAtLeastOneExists(hasText("snpack"))
        onNodeWithContentDescription("Instellingen").performClick()
        createScreenshot("${prefix}_settings")

        activity.onBackPressedDispatcher.onBackPressed()
    }

    private fun createScreenshot(name: String) = composeTestRule.onRoot()
        .captureRoboImage("../fastlane/metadata/android/nl-NL/images/phoneScreenshots/${atomic}-$name.png")
}
