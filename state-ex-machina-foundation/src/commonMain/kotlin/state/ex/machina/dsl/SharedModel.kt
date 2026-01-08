package state.ex.machina.dsl

import kotlinx.coroutines.flow.MutableSharedFlow
import state.ex.machina.foundation.Intent
import state.ex.machina.foundation.SharedModel
import state.ex.machina.foundation.State
import state.ex.machina.foundation.Store

fun <S : State, I : Intent> SharedModel(
    store: Store<S>,
): SharedModel<S, I> = DefaultSharedModel(store)

internal class DefaultSharedModel<S : State, I : Intent>(
    store: Store<S>,
) : SharedModel<S, I> {
    override val intents = MutableSharedFlow<I>()
    override val states = store.stateFlow
}
