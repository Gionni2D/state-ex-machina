package com.gionni2d.mvi.extension

import com.gionni2d.mvi.foundation.Reducer
import com.gionni2d.mvi.foundation.State

class ReducerMapper<A : State, B : State>(
    internal val getter: A.() -> B,
    private val updater: A.(B) -> A
) {
    fun map(reducer: Reducer<B>): Reducer<A> = reducer.map(
        getter = getter,
        updater = updater
    )
}