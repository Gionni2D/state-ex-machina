package com.gionni2d.mvi.utils

import android.util.Log
import com.gionni2d.mvi.Constants

internal object Logger {
    fun w(message: String) = Log.w(Constants.Logger.TAG, message)
}