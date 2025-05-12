package state.ex.machina.extension

import state.ex.machina.foundation.Reducer
import state.ex.machina.foundation.State

/**
 * Create a single reducer from the concatenation of multiple (optional) reducer.
 * This helps to emit only a single [state update event][state.ex.machina.foundation.Store.stateFlow]
 * instead of one for each reducer
 */
fun <S : State> concatReducers(
    vararg reducers: Reducer<S>?
) = Reducer<S> { s ->
    reducers.filterNotNull().fold(s) { accS, reducer -> reducer.reduce(accS) }
}

fun <S : State> Reducer.Companion.identity() = Reducer<S> { it }

fun <A : State, B : State> Reducer<B>.map(
    getter: A.() -> B,
    updater: A.(B) -> A
): Reducer<A> = Reducer { s1: A ->
    s1.updater(this.reduce(s1.getter()))
}
