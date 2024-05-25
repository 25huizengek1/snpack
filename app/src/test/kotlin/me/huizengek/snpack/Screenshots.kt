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
import org.junit.Before
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

    @Before
    fun prepare() = with(composeTestRule) {
        onNodeWithText(composeTestRule.activity.getString(R.string.app_label)).assertIsDisplayed()
    }.let { }

    @Test
    @Config(qualifiers = "+en")
    fun testTakeScreenshotsEnglish() = takeAll("en-US")

    @Test
    @Config(qualifiers = "+nl")
    fun testTakeScreenshotsDutch() = takeAll("nl-NL")

    private fun takeAll(tag: String) {
        ThemePreferences.theme = ThemePreferences.Theme.LIGHT
        ThemePreferences.isDynamic = false
        take("light_static", tag)
        ThemePreferences.isDynamic = true
        take("light_dynamic", tag)

        ThemePreferences.theme = ThemePreferences.Theme.DARK
        ThemePreferences.isDynamic = false
        take("dark_static", tag)
        ThemePreferences.isDynamic = true
        take("dark_dynamic", tag)

        atomic = 1
    }

    @OptIn(ExperimentalTestApi::class)
    private fun take(prefix: String, tag: String) = with(composeTestRule) {
        createScreenshot("${prefix}_home", tag)

        waitUntilAtLeastOneExists(hasTestTag("homeFab"))
        onNodeWithTag("homeFab").performClick()
        waitUntilAtLeastOneExists(hasText(activity.getString(R.string.save)))
        createScreenshot("${prefix}_new_pack", tag)

        activity.onBackPressedDispatcher.onBackPressed()
        waitUntilAtLeastOneExists(hasText(activity.getString(R.string.app_label)))
        onNodeWithContentDescription(activity.getString(R.string.settings)).performClick()
        createScreenshot("${prefix}_settings", tag)

        activity.onBackPressedDispatcher.onBackPressed()
    }

    private fun createScreenshot(name: String, tag: String) = composeTestRule.onRoot()
        .captureRoboImage("../fastlane/metadata/android/$tag/images/phoneScreenshots/$atomic-$name.png")
}
