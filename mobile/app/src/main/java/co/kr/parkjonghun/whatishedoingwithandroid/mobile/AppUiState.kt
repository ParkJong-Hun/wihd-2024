package co.kr.parkjonghun.whatishedoingwithandroid.mobile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import co.kr.parkjonghun.whatishedoingwithandroid.base.usecase.statemachine.StateMachine
import co.kr.parkjonghun.whatishedoingwithandroid.service.usecase.statemachine.login.LoginAction
import co.kr.parkjonghun.whatishedoingwithandroid.service.usecase.statemachine.login.LoginState
import co.kr.parkjonghun.whatishedoingwithandroid.ui.base.Intent
import co.kr.parkjonghun.whatishedoingwithandroid.ui.base.UiState
import kotlinx.coroutines.flow.map
import org.koin.compose.rememberKoinInject

@Immutable
data class AppUiState(
    val isShowLoading: Boolean,
    val error: Throwable?,
    val isShowSomething: Boolean,
) : UiState {
    val isShowError: Boolean = error != null
}

@Composable
fun rememberAppUiState(): Pair<State<AppUiState>, AppIntent> {
    val stateMachine = rememberKoinInject<StateMachine<LoginState, LoginAction>>()
    val state = remember {
        AppReducer(stateMachine).appUiState
    }.collectAsState(
        initial = AppUiState(
            isShowLoading = false,
            error = null,
            isShowSomething = false,
        ),
    )
    val intent = remember {
        AppIntent
    }
    return state to intent
}

@Stable
class AppReducer(
    stateMachine: StateMachine<LoginState, LoginAction>,
) {
    val appUiState = stateMachine.flow.map { domainState ->
        when (domainState) {
            is LoginState.None -> {
                AppUiState(
                    isShowLoading = false,
                    error = null,
                    isShowSomething = false,
                )
            }

            is LoginState.Loading -> {
                AppUiState(
                    isShowLoading = true,
                    error = null,
                    isShowSomething = false,
                )
            }

            is LoginState.Success -> {
                AppUiState(
                    isShowLoading = false,
                    error = null,
                    isShowSomething = true,
                )
            }

            is LoginState.Error -> {
                AppUiState(
                    isShowLoading = false,
                    error = null,
                    isShowSomething = true,
                )
            }
        }
    }
}

class AppIntent(
    private val stateMachine: StateMachine<AppState, AppAction>,
) : Intent {
    fun loginSuccess() {
        stateMachine.dispatch(AppAction.AfterLoginSuccess)
    }
}