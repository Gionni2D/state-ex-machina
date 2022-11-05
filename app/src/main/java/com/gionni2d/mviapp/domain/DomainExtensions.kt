package com.gionni2d.mviapp.domain

import kotlinx.coroutines.flow.Flow

typealias ResultFlow<T> = Flow<Result<T>>

fun <I> I.success() : Result<I> = Result.success(this)

fun <I, E : Throwable> E.failure() : Result<I> = Result.failure(this)