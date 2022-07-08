package com.gionni2d.mvi

import kotlinx.coroutines.flow.*

interface State

/**
 * constraint: pure function
 */
fun interface Reducer<S : State, I : Intent> {
    fun reduce(state: S, intent: I): S
}

fun interface Model<S : State, I : Intent> {
    fun subscribeTo(intents: Flow<I>): StateFlow<S>
}

inline fun <S : State, I : Intent, reified SI : I> Flow<I>.applyReducer(
    store: Store<S>,
    reducer: Reducer<S, SI>
) : Flow<I> = this.onEach { intent ->
    (intent as? SI)?.let { store.update { reducer.reduce(it, intent) } }
}
