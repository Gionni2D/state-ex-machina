package com.gionni2d.mvi.extension

import com.gionni2d.mvi.foundation.Reducer
import com.gionni2d.mvi.foundation.State

/**
 * Create a single reducer from the concatenation of multiple (optional) reducer.
 * This helps to emit only a single [state update event][com.gionni2d.mvi.foundation.Store.stateFlow]
 * instead of one for each reducer
 */
fun <S : State> concatReducers(
    vararg reducers: Reducer<S>?
) = Reducer<S> { s ->
    reducers.filterNotNull().fold(s) { accS, reducer -> reducer.reduce(accS) }
}
