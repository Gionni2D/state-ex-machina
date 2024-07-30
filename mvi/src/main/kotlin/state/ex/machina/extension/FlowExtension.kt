package state.ex.machina.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import state.ex.machina.foundation.Reducer
import state.ex.machina.foundation.State
import state.ex.machina.foundation.Store

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
