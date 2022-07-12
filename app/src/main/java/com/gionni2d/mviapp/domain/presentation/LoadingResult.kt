package com.gionni2d.mviapp.domain.presentation

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed interface LoadingResult<out R, out E> {
    object Loading : LoadingResult<Nothing, Nothing>
    data class Success<out R>(val value: R) : LoadingResult<R, Nothing>
    data class Failure<out E>(val error: E) : LoadingResult<Nothing, E>
}

fun LoadingResult<*, *>.isLoading() = this is LoadingResult.Loading

@OptIn(ExperimentalContracts::class)
fun <R> LoadingResult<R, *>.isSuccess() : Boolean {
    contract { returns(true) implies (this@isSuccess is LoadingResult.Success) }
    return this is LoadingResult.Success
}

@OptIn(ExperimentalContracts::class)
fun <E> LoadingResult<*, E>.isFailure() : Boolean {
    contract { returns(true) implies (this@isFailure is LoadingResult.Failure) }
    return this is LoadingResult.Failure
}
