package state.ex.machina.dsl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import state.ex.machina.foundation.Effect
import state.ex.machina.foundation.EffectReceiver

class EffectManager<E : Effect> : EffectReceiver<E> {
    private val effectSender = MutableSharedFlow<E>()

    override val effectFlow: Flow<E> = effectSender.asSharedFlow()

    suspend fun emit(effect: E) = effectSender.emit(effect)
}

suspend fun <E: Effect> effectsHandler(
    effects: Flow<E>,
    builder: EffectReceiverScope<E>.() -> Unit
) {
    EffectsHandler(effects)
        .apply(builder)
        .effectHandlers
        .merge()
        .collect()
}

private class EffectsHandler<E : Effect>(
    private val effects: Flow<E>
) : EffectReceiverScope<E>() {
    val effectHandlers = mutableListOf<Flow<*>>()

    override fun on(
        predicate: suspend (E) -> Boolean,
        handler: suspend (E) -> Unit
    ) {
        effectHandlers += effects.filter(predicate).onEach(handler)
    }
}