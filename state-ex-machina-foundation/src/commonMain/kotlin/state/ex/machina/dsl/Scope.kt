package state.ex.machina.dsl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import state.ex.machina.extension.ReducerMapper
import state.ex.machina.extension.concatReducers
import state.ex.machina.foundation.Effect
import state.ex.machina.foundation.Intent
import state.ex.machina.foundation.Reducer
import state.ex.machina.foundation.State

///////////////////////////
//// Scope definitions ////
///////////////////////////

interface StateMachineScope<S : State> {
    fun <D> on(flow: Flow<D>): ActionScope<S, D>
}

abstract class IntentStateMachineScope<S : State, I : Intent>(
    val intents: Flow<I>
) : StateMachineScope<S> {
    inline fun <reified I2> on() = on(intents.filterIsInstance<I2>())

    fun on(predicate: (I) -> Boolean) = on(intents.filter(predicate))
}

interface ActionScope<S : State, D> {
    infix fun updateState(reducerBuilder: (D) -> Reducer<S>)

    infix fun sideEffect(block: suspend SideEffectScope<S>.(D) -> Unit)

    infix fun rawSideEffect(block: SideEffectScope<S>.(Flow<D>) -> Flow<*>)
}

interface SideEffectScope<S : State> {
    val currentState: S

    fun updateState(reducer: Reducer<S>): S
}

class EffectReceiverScope<E : Effect>(
    val effects: Flow<E> // TODO: find a way to make it private
) {
    val effectHandlers = mutableListOf<Flow<*>>() // TODO: find a way to make it non-mutable

    inline fun <reified E2> on(
        noinline handler: suspend (E2) -> Unit
    ) {
        effectHandlers += effects.filterIsInstance<E2>().onEach(handler)
    }

    fun on(
        predicate: suspend (E) -> Boolean,
        handler: suspend (E) -> Unit
    ) {
        effectHandlers += effects.filter(predicate).onEach(handler)
    }
}

//////////////////////////
//// Scope extensions ////
//////////////////////////

fun <S : State> StateMachineScope<S>.onLaunched() = on(flowOf(Unit))

fun <S : State> StateMachineScope<S>.launchedEffect(
    block: suspend SideEffectScope<S>.() -> Unit
) = on(flowOf(Unit)) sideEffect { block() }

infix fun <S : State> ActionScope<S, *>.updateState(reducer: Reducer<S>) = updateState { reducer }

infix fun <S : State, D> ActionScope<S, D>.getStateAndUpdate(reducer: (S, D) -> S) =
    updateState { d -> Reducer { s -> reducer(s, d) } }

fun <S : State> ActionScope<S, *>.updateState(vararg reducers: Reducer<S>?) =
    updateState(concatReducers(*reducers))

fun <S : State> SideEffectScope<S>.updateState(vararg reducers: Reducer<S>?) =
    updateState(concatReducers(*reducers))

fun <S : State, STo : State> SideEffectScope<S>.map(
    reducerMapper: ReducerMapper<S, STo>
): SideEffectScope<STo> = object : SideEffectScope<STo> {
    override val currentState: STo
        get() = reducerMapper.getter(this@map.currentState)

    override fun updateState(reducer: Reducer<STo>): STo {
        val sReducer = reducerMapper.map(reducer)
        val sUpdated = updateState(sReducer)
        return reducerMapper.getter(sUpdated)
    }
}
