package com.gionni2d.mvi.dsl

import com.gionni2d.mvi.extension.concatReducers
import com.gionni2d.mvi.foundation.Intent
import com.gionni2d.mvi.foundation.Reducer
import com.gionni2d.mvi.foundation.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance

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

    fun on(intentFilterPredicate: (I) -> Boolean) = on(intents.filter(intentFilterPredicate))
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

//////////////////////////
//// Scope extensions ////
//////////////////////////

infix fun <S : State> ActionScope<S, *>.updateState(reducer: Reducer<S>) = updateState { reducer }

fun <S : State> ActionScope<S, *>.updateState(vararg reducers: Reducer<S>?) = updateState(concatReducers(*reducers))

fun <S : State> SideEffectScope<S>.updateState(vararg reducers: Reducer<S>?) = updateState(concatReducers(*reducers))
