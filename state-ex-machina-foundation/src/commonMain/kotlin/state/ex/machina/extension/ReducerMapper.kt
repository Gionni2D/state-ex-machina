package state.ex.machina.extension

import state.ex.machina.foundation.Reducer
import state.ex.machina.foundation.State

class ReducerMapper<A : State, B : State>(
    internal val getter: A.() -> B,
    private val updater: A.(B) -> A
) {
    fun map(reducer: Reducer<B>): Reducer<A> = reducer.map(
        getter = getter,
        updater = updater
    )
}