package co.kr.parkjonghun.whatishedoingwithandroid.base.usecase.statemachine.sample

import co.kr.parkjonghun.whatishedoingwithandroid.base.usecase.statemachine.StateMachine
import co.kr.parkjonghun.whatishedoingwithandroid.base.usecase.statemachine.createStateMachine

fun createSampleStateMachine(
    sideEffectCreator: StateMachine.SideEffectCreator<SampleSideEffect, SampleState, SampleAction>,
    reactiveEffect: StateMachine.ReactiveEffect<SampleState, SampleAction>,
    initialState: SampleState?,
) = createStateMachine(
    name = "Sample",
    initialState = initialState ?: SampleState.None,
    sideEffectCreator = sideEffectCreator,
    reactiveEffect = reactiveEffect,
) {
    fromState<SampleState.None> {
        on<SampleAction.Do> {
            transitionTo(SampleState.Loading)
        }
    }

    fromState<SampleState.Loading> {
        on<SampleAction.Succeed> {
            transitionTo(SampleState.Stable.Waiting(it.data))
        }
        on<SampleAction.Fail> {
            transitionTo(SampleState.Error(it.throwable))
        }
    }

    fromState<SampleState.Error> {
        on<SampleAction.Retry> {
            transitionTo(SampleState.Loading)
        }

        on<SampleAction.ResolveError> {
            transitionTo(SampleState.None)
        }
    }

    fromState<SampleState.Stable.Waiting> {
        on<SampleAction.ResolveError> {
            transitionTo(SampleState.None)
        }
    }
}