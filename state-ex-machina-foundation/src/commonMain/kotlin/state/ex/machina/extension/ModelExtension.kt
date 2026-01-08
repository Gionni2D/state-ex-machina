package state.ex.machina.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import state.ex.machina.foundation.Intent
import state.ex.machina.foundation.Model
import state.ex.machina.foundation.SharedModel
import state.ex.machina.foundation.State

/**
 * Utility to use a [SharedModel] as a [Model]
 */
fun <S : State, I : Intent> SharedModel<S, I>.asModel(
    coroutineScope: CoroutineScope
) = object : Model<S, I> {
    override fun subscribeTo(intents: Flow<I>): StateFlow<S> {
        coroutineScope.launch {
            this@asModel.intents.emitAll(intents)
        }
        return states
    }
}

/**
 * Utility to use [Model] as a [SharedModel] by creating a new [MutableSharedFlow] as the
 * [intent flow][SharedModel.intents] and using it to obtain the [state flow][SharedModel.states]
 * towards the [Model.subscribeTo] method
 */
fun <S : State, I : Intent> Model<S, I>.asSharedModel(): SharedModel<S, I> = object : SharedModel<S, I> {
    override val intents = MutableSharedFlow<I>()
    override val states = subscribeTo(intents)
}
