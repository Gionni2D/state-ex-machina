package com.gionni2d.mvi.dsl

import com.gionni2d.mvi.foundation.Intent
import com.gionni2d.mvi.foundation.Reducer
import com.gionni2d.mvi.foundation.State
import com.gionni2d.mvi.foundation.Store
import com.gionni2d.mvi.foundation.store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import com.gionni2d.mvi.dsl.ActionScope as ActionScope

fun <S : State, I : Intent> stateMachine(
    initialState: S,
    intents: Flow<I>,
    coroutineScope: CoroutineScope,
    block: IntentStateMachineScope<S, I>.() -> Unit
) = stateMachine(store(initialState), intents, coroutineScope, block)

fun <S : State, I : Intent> stateMachine(
    store: Store<S>,
    intents: Flow<I>,
    coroutineScope: CoroutineScope,
    builder: IntentStateMachineScope<S, I>.() -> Unit
): StateFlow<S> {
    StateMachineBuilder(store, intents)
        .apply(builder)
        .actionHandlers
        .merge()
        .launchIn(coroutineScope)

    return store.stateFlow
}

class StateMachineBuilder<S : State, I : Intent>(
    private val store: Store<S>,
    intents: Flow<I>
) : IntentStateMachineScope<S, I>(intents), SideEffectScope<S> {

    private val _actionHandlers = mutableListOf<Flow<*>>()
    val actionHandlers: List<Flow<*>>
        get() = _actionHandlers.toList()

    override val currentState: S by store::currentState
    override fun updateState(reducer: Reducer<S>): S = store.update(reducer)

    override fun <D> on(flow: Flow<D>) = ActionHandler(flow)

    inner class ActionHandler<D>(
        val flow: Flow<D>
    ) : ActionScope<S, D> {
        override fun updateState(
            reducerBuilder: (D) -> Reducer<S>
        ) = sideEffect { updateState(reducerBuilder(it)) }

        override fun sideEffect(
            block: suspend SideEffectScope<S>.(D) -> Unit
        ) = rawSideEffect { flow -> flow.onEach { block(it) } }

        override fun rawSideEffect(
            block: SideEffectScope<S>.(Flow<D>) -> Flow<*>
        ) {
            _actionHandlers += block(flow)
        }
    }
}
