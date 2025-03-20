package state.ex.machina.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import state.ex.machina.foundation.Intent
import state.ex.machina.foundation.Model
import state.ex.machina.foundation.State

private fun <I : Intent> intentsFlowForCompose(
    scope: CoroutineScope
): Pair<(I) -> Unit, Flow<I>> {
    val intentFlow = MutableSharedFlow<I>()
    val intentCallback: (I) -> Unit = { intent ->
        scope.launch {
            intentFlow.emit(intent)
        }
    }
    return intentCallback to intentFlow
}

data class StateMachineComposeContract<S : State, I : Intent>(
    val state: S,
    val onIntent: (I) -> Unit
)

@Composable
fun <S : State, I : Intent> rememberStateMachine(
    model: Model<S, I>,
    scope: CoroutineScope = rememberCoroutineScope()
): StateMachineComposeContract<S, I> {
    val (stateFlow, onIntent) = remember {
        val (onIntent, intentFlow) = intentsFlowForCompose<I>(scope)
        val stateFlow = model.subscribeTo(intentFlow)
        stateFlow to onIntent
    }
    val state = stateFlow.collectAsState()

    return StateMachineComposeContract(
        state = state.value,
        onIntent = onIntent
    )
}
