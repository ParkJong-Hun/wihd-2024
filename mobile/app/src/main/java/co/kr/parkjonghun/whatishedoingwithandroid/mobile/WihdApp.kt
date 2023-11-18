package co.kr.parkjonghun.whatishedoingwithandroid.mobile

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import co.kr.parkjonghun.whatishedoingwithandroid.main.ui.mainScreen
import co.kr.parkjonghun.whatishedoingwithandroid.ui.theme.MobileTheme

/**
 * Top level composable.
 */
@Composable
fun WihdApp(
    windowSizeClass: WindowSizeClass,
    appState: WihdAppState = rememberWihdAppState(windowSizeClass = windowSizeClass),
) {
    MobileTheme {
        Surface(
            modifier = Modifier,
            color = MaterialTheme.colorScheme.background,
        ) {
            WihdNavHost()
        }
    }
}

@Composable
private fun WihdNavHost(
    navController: NavHostController = rememberNavController(),
    // TODO
) {
    NavHost(navController = navController, startDestination = "") {
        mainScreen()
    }
}

private fun NavGraphBuilder.mainScreen(
    navController: NavHostController
) {
    mainScreen(
        // TODO
    )
}