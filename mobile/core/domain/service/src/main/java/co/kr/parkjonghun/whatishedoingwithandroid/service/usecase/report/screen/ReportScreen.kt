package co.kr.parkjonghun.whatishedoingwithandroid.service.usecase.report.screen

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import co.kr.parkjonghun.whatishedoingwithandroid.base.usecase.UseCase
import co.kr.parkjonghun.whatishedoingwithandroid.service.gateway.provider.AnalyticsProvider
import org.koin.compose.koinInject

/**
 * Report ScreenView to the outside.
 */
interface ReportScreen : UseCase {
    operator fun invoke(screenName: String)
}

class ReportScreenImpl(
    private val analyticsProvider: AnalyticsProvider,
) : ReportScreen {
    override operator fun invoke(screenName: String) {
        analyticsProvider.logEvent(SCREEN_NAME, mapOf("screen_name" to screenName))
    }

    companion object {
        private const val SCREEN_NAME = "screen_name"
    }
}

/**
 * Report ScreenView in the Composable function to the out side.
 */
@SuppressLint("ComposableNaming")
@Composable
fun ReportScreen(name: String) {
    val reportScreenUseCase = koinInject<ReportScreen>()
    LaunchedEffect(Unit) { reportScreenUseCase(name) }
}