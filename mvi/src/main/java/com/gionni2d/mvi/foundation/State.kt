package com.gionni2d.mvi.foundation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface State

/**
 * Pure function to define a state update
 */
fun interface Reducer<S : State> {
    fun reduce(state: S): S

    companion object
}

/**
 * Component that hold the [current state][currentState] and allow its [update]
 */
interface Store<S : State> {
    val currentState: S
    val stateFlow: StateFlow<S>

    fun update(transform: Reducer<S>): S
}

private class FlowStore<S : State>(initialValue: S) : Store<S> {
    private val _stateFlow = MutableStateFlow(initialValue)

    override val currentState: S by _stateFlow::value
    override val stateFlow: StateFlow<S> = _stateFlow.asStateFlow()

    override fun update(reducer: Reducer<S>): S = _stateFlow
        .update(reducer::reduce)
        .let { currentState }
}

fun <S : State> store(initialValue: S): Store<S> = FlowStore(initialValue)
