package com.gionni2d.mvi.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gionni2d.mvi.Intent
import com.gionni2d.mvi.Model
import com.gionni2d.mvi.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull

fun <I : Intent> intentsFlowForCompose(): Pair<(I) -> Unit, Flow<I>> {
    val stateFlow = MutableStateFlow<I?>(null)
    val callback: (I) -> Unit = { stateFlow.value = it }
    return callback to stateFlow.filterNotNull()
}

data class MviComponents<S : State, I : Intent>(
    val stateFlow: StateFlow<S>,
    val onIntent: (I) -> Unit
)

@Composable
fun <S: State, I: Intent> rememberMviComponent(
    model: Model<S, I>
) : MviComponents<S, I> {
    return remember {
        val (onIntent, intentFlow) = intentsFlowForCompose<I>()
        val stateFlow = model.subscribeTo(intentFlow)
        MviComponents(
            stateFlow = stateFlow,
            onIntent = onIntent
        )
    }
}
