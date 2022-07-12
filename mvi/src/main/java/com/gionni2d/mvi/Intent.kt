package com.gionni2d.mvi

/**
 * Interaction towards the [model][Model], which can lead to a change
 * of [state][State] or to a side effect.
 */
sealed interface Action

/**
 * Interaction from the user towards the [model][Model]
 * (translation from the user's mental model to the app model)
 */
interface Intent: Action

/**
 * Interaction from a layer other than the presentation layer towards the [model][Model]
 */
interface Event: Action
