package com.gionni2d.mvi.extension

import com.gionni2d.mvi.foundation.Reducer
import com.gionni2d.mvi.foundation.State
import com.gionni2d.mvi.foundation.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

fun <S : State, D> Flow<D>.applyReducer(
    store: Store<S>,
    reducer: Reducer<S>
): Flow<D> = applyReducer(
    store = store,
    reducerFactory =  { reducer }
)

fun <S : State, D> Flow<D>.applyReducer(
    store: Store<S>,
    reducerFactory: (D) -> Reducer<S>
): Flow<D> = this.onEach { data ->
    store.update { reducerFactory(data).reduce(it) }
}
