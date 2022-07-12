package com.gionni2d.mvi

import kotlinx.coroutines.flow.*

interface State

/**
 * constraint: pure function
 */
fun interface Reducer<S : State, A : Action> {
    fun reduce(state: S, action: A): S
}

fun interface Model<S : State, I : Intent> {
    fun subscribeTo(intents: Flow<I>): StateFlow<S>
}

inline fun <S : State, A : Action, reified SA : A> Flow<A>.applyReducer(
    store: Store<S>,
    reducer: Reducer<S, SA>
) : Flow<A> = this.onEach { action ->
    (action as? SA)?.let { store.update { reducer.reduce(it, action) } }
}
