package state.ex.machina.dsl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import state.ex.machina.foundation.Effect
import state.ex.machina.foundation.EffectReceiver

class EffectManager<E : Effect> : EffectReceiver<E> {
    private val effectSender = MutableSharedFlow<E>()

    override val effectFlow: Flow<E> = effectSender.asSharedFlow()

    suspend fun emit(effect: E) = effectSender.emit(effect)
}