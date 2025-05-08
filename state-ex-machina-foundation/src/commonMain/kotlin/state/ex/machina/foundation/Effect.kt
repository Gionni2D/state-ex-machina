package state.ex.machina.foundation

import kotlinx.coroutines.flow.Flow

interface Effect

interface EffectReceiver<E : Effect> {
    val effectFlow: Flow<E>
}
