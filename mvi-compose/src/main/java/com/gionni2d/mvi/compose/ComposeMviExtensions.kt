package com.gionni2d.mvi.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.gionni2d.mvi.foundation.Intent
import com.gionni2d.mvi.foundation.Model
import com.gionni2d.mvi.foundation.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

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

data class MviComponents<S : State, I : Intent>(
    val state: S,
    val onIntent: (I) -> Unit
)

@Composable
fun <S : State, I : Intent> rememberMviComponent(
    model: Model<S, I>,
    scope: CoroutineScope = rememberCoroutineScope()
): MviComponents<S, I> {
    val (stateFlow, onIntent) = remember {
        val (onIntent, intentFlow) = intentsFlowForCompose<I>(scope)
        val stateFlow = model.subscribeTo(intentFlow)
        stateFlow to onIntent
    }
    val state = stateFlow.collectAsState()

    return MviComponents(
        state = state.value,
        onIntent = onIntent
    )
}
