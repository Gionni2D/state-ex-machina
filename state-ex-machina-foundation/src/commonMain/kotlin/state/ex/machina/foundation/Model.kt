package state.ex.machina.foundation

import kotlinx.coroutines.flow.*

fun interface Model<S : State, I : Intent> {
    fun subscribeTo(intents: Flow<I>): StateFlow<S>
}

interface SharedModel<S : State, I : Intent> {
    val intents: MutableSharedFlow<I>
    val states: StateFlow<S>
}
