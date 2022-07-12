package com.gionni2d.mviapp.domain.presentation.compose

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gionni2d.mviapp.R

data class StringResource(
    @StringRes val id: Int,
    val args: List<Any> = emptyList(),
) {
    @Composable
    fun toLocalizedString() = stringResource(id = id, formatArgs = args.toTypedArray())
    
    companion object {
        fun from(value: String) = StringResource(R.string.replacement, listOf(value))

        val Empty = from("")
    }
}