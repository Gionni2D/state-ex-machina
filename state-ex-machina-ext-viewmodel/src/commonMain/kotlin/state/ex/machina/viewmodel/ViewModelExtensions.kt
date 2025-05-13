package state.ex.machina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import state.ex.machina.dsl.IntentStateMachineScope
import state.ex.machina.dsl.stateMachine
import state.ex.machina.foundation.Intent
import state.ex.machina.foundation.State
import state.ex.machina.foundation.Store

fun <S : State, I : Intent> ViewModel.stateMachine(
    store: Store<S>,
    intents: Flow<I>,
    builder: IntentStateMachineScope<S, I>.() -> Unit
): StateFlow<S> = stateMachine(
    store = store,
    intents = intents,
    coroutineScope = viewModelScope,
    builder = builder,
)