package com.gionni2d.mvi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gionni2d.mvi.dsl.IntentStateMachineScope
import com.gionni2d.mvi.foundation.Intent
import com.gionni2d.mvi.foundation.State
import com.gionni2d.mvi.foundation.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import com.gionni2d.mvi.dsl.stateMachine

fun <S : State, I : Intent> ViewModel.stateMachine(
    initialState: S,
    intents: Flow<I>,
    builder: IntentStateMachineScope<S, I>.() -> Unit
): StateFlow<S> = stateMachine(
    initialState = initialState,
    intents = intents,
    coroutineScope = viewModelScope,
    builder = builder,
)

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