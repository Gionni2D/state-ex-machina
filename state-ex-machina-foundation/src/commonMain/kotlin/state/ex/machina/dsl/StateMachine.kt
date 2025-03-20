package state.ex.machina.dsl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import state.ex.machina.foundation.Intent
import state.ex.machina.foundation.Reducer
import state.ex.machina.foundation.State
import state.ex.machina.foundation.Store

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

        @OptIn(ExperimentalCoroutinesApi::class)
        override fun sideEffect(
            block: suspend SideEffectScope<S>.(D) -> Unit
        ) = rawSideEffect { flow ->
            flow.flatMapMerge { flow<Nothing> { block(it) } }
        }

        override fun rawSideEffect(
            block: SideEffectScope<S>.(Flow<D>) -> Flow<*>
        ) {
            _actionHandlers += block(flow)
        }
    }
}
