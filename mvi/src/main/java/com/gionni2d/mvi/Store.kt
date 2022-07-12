package com.gionni2d.mvi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface Store<S : State> {
    val currentState: S
    val stateFlow: StateFlow<S>

    fun update(transform: (S) -> S): S
}

private class FlowStore<S : State>(initialValue: S) : Store<S> {
    private val _stateFlow = MutableStateFlow(initialValue)

    override val currentState: S by _stateFlow::value
    override val stateFlow: StateFlow<S> = _stateFlow.asStateFlow()

    override fun update(transform: (S) -> S): S = _stateFlow.update(transform).let { currentState }
}

fun <S : State> store(initialValue: S): Store<S> = FlowStore(initialValue)