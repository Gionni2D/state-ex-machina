package com.gionni2d.mvi.foundation

/**
 * Interaction from the user towards the [model][Model]
 * (translation from the user's mental model to the app model), which can lead to a change
 * of [state][State] (applied by a reducer) or to a side effect.
 */
interface Intent
