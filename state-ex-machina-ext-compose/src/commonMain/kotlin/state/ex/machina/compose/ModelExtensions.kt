package state.ex.machina.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import state.ex.machina.extension.asSharedModel
import state.ex.machina.foundation.Intent
import state.ex.machina.foundation.Model
import state.ex.machina.foundation.SharedModel
import state.ex.machina.foundation.State

interface StateMachineComposeContract<S : State, I : Intent> {
    val state: S
    val onIntent: (I) -> Unit

    operator fun component1(): S = state
    operator fun component2(): (I) -> Unit = onIntent
}

@Composable
fun <S : State, I : Intent> rememberStateMachine(
    model: Model<S, I>,
    scope: CoroutineScope = rememberCoroutineScope()
): StateMachineComposeContract<S, I> = rememberStateMachine(
    scope = scope,
    intentsBuilder = { MutableSharedFlow() },
    statesBuilder = { intents -> model.subscribeTo(intents) },
    key = model,
)

@Composable
fun <S : State, I : Intent> rememberStateMachine(
    model: SharedModel<S, I>,
    scope: CoroutineScope = rememberCoroutineScope()
): StateMachineComposeContract<S, I> = rememberStateMachine(
    scope = scope,
    intentsBuilder = { model.intents },
    statesBuilder = { model.states },
    key = model,
)

@Composable
fun <S : State, I : Intent> rememberSharedStateMachine(
    model: Model<S, I>,
    scope: CoroutineScope = rememberCoroutineScope()
): StateMachineComposeContract<S, I> = rememberStateMachine(
    model = remember(model) { model.asSharedModel() },
    scope = scope,
)

@Composable
private fun <I : Intent, S : State> rememberStateMachine(
    scope: CoroutineScope,
    intentsBuilder: () -> MutableSharedFlow<I>,
    statesBuilder: (MutableSharedFlow<I>) -> StateFlow<S>,
    key: Any?,
): StateMachineComposeContract<S, I> {
    val (states, onIntent) = remember(key, scope) {
        val intents = intentsBuilder()
        val states = statesBuilder(intents)
        val onIntent: (I) -> Unit = { intent ->
            scope.launch {
                intents.emit(intent)
            }
        }

        states to onIntent
    }
    val state = states.collectAsState()

    return object : StateMachineComposeContract<S, I> {
        override val state: S = state.value
        override val onIntent = onIntent
    }
}
