/**
 *  https://github.com/ParkJong-Hun/nowinandroid/blob/main/core/testing/src/main/kotlin/com/google/samples/apps/nowinandroid/core/testing/util/ScreenshotHelper.kt
 */
package co.kr.parkjonghun.whatishedoingwithandroid.mobile.testing

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import co.kr.parkjonghun.whatishedoingwithandroid.system.theme.MobileTheme
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziOptions.CompareOptions
import com.github.takahirom.roborazzi.RoborazziOptions.RecordOptions
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.accompanist.testharness.TestHarness
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalRoborazziApi::class)
val DefaultRoborazziOptions =
    RoborazziOptions(
        compareOptions = CompareOptions(changeThreshold = 0f),
        recordOptions = RecordOptions(resizeScale = 1.0),
    )

enum class DefaultTestDevices(val description: String, val spec: String) {
    PHONE("phone", "spec:shape=Normal,width=640,height=360,unit=dp,dpi=480"),
    FOLDABLE("foldable", "spec:shape=Normal,width=673,height=841,unit=dp,dpi=480"),
    TABLET("tablet", "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480"),
}

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureMultiDevice(
    screenshotName: String,
    body: @Composable () -> Unit,
) {
    DefaultTestDevices.entries.forEach {
        this.captureForDevice(it.description, it.spec, screenshotName, body = body)
    }
}

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureForDevice(
    deviceName: String,
    deviceSpec: String,
    screenshotName: String,
    roborazziOptions: RoborazziOptions = DefaultRoborazziOptions,
    darkMode: Boolean = false,
    body: @Composable () -> Unit,
) {
    val (width, height, dpi) = extractSpecs(deviceSpec)

    // Set qualifiers from specs
    RuntimeEnvironment.setQualifiers("w${width}dp-h${height}dp-${dpi}dpi")

    this.activity.setContent {
        CompositionLocalProvider(
            LocalInspectionMode provides true,
        ) {
            TestHarness(darkMode = darkMode) {
                body()
            }
        }
    }
    this.onRoot()
        .captureRoboImage(
            "build/outputs/roborazzi/${screenshotName}_$deviceName.png",
            roborazziOptions = roborazziOptions,
        )
}

/**
 * Takes six screenshots combining light/dark and default/Android themes and whether dynamic color
 * is enabled.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureMultiTheme(
    name: String,
    overrideFileName: String? = null,
    shouldCompareDarkMode: Boolean = true,
    content: @Composable (desc: String) -> Unit,
) {
    val darkModeValues = if (shouldCompareDarkMode) listOf(true, false) else listOf(false)

    var darkMode by mutableStateOf(true)

    this.setContent {
        CompositionLocalProvider(
            LocalInspectionMode provides true,
        ) {
            MobileTheme(
                useDarkTheme = darkMode,
            ) {
                // Keying is necessary in some cases (e.g. animations)
                key(darkMode) {
                    val description = generateDescription(
                        shouldCompareDarkMode,
                        darkMode,
                    )
                    content(description)
                }
            }
        }
    }

    // Create permutations
    darkModeValues.forEach { isDarkMode ->
        darkMode = isDarkMode
        val darkModeDesc = if (isDarkMode) "dark" else "light"

        val filename = overrideFileName ?: name

        this.onRoot()
            .captureRoboImage(
                "build/outputs/roborazzi/" +
                    "${name}_$filename" +
                    "_$darkModeDesc" +
                    ".png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }
}

@Composable
private fun generateDescription(
    shouldCompareDarkMode: Boolean,
    darkMode: Boolean,
): String {
    val description = "" +
        if (shouldCompareDarkMode) {
            if (darkMode) "Dark" else "Light"
        } else {
            ""
        }

    return description.trim()
}

/**
 * Extracts some properties from the spec string. Note that this function is not exhaustive.
 */
private fun extractSpecs(deviceSpec: String): TestDeviceSpecs {
    val specs = deviceSpec.substringAfter("spec:")
        .split(",").map { it.split("=") }.associate { it[0] to it[1] }
    val width = specs["width"]?.toInt() ?: 640
    val height = specs["height"]?.toInt() ?: 480
    val dpi = specs["dpi"]?.toInt() ?: 480
    return TestDeviceSpecs(width, height, dpi)
}

data class TestDeviceSpecs(val width: Int, val height: Int, val dpi: Int)
