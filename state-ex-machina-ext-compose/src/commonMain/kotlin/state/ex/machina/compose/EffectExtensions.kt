package state.ex.machina.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import state.ex.machina.dsl.EffectReceiverScope
import state.ex.machina.dsl.effectsHandler
import state.ex.machina.foundation.Effect
import state.ex.machina.foundation.EffectReceiver
import state.ex.machina.foundation.Intent
import state.ex.machina.foundation.Model
import state.ex.machina.foundation.State

@Composable
fun <E : Effect> EffectReceiver<E>.collectEffect(
    builder: EffectReceiverScope<E>.() -> Unit
) {
    LaunchedEffect(effectFlow) {
        effectsHandler(effectFlow, builder)
    }
}

@Composable
fun <S : State, I : Intent, E : Effect, M> rememberEffectStateMachine(
    model: M,
    scope: CoroutineScope = rememberCoroutineScope(),
    effectHandlerBuilder: EffectReceiverScope<E>.() -> Unit
): StateMachineComposeContract<S, I> where M: Model<S, I>, M: EffectReceiver<E> {
    model.collectEffect(effectHandlerBuilder)
    return rememberStateMachine(model, scope)
}

