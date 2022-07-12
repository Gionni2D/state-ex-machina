package com.gionni2d.mviapp.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Spacer4() {
    BaseSpacer(size = 4.dp)
}

@Composable
private fun BaseSpacer(size: Dp) {
    Spacer(modifier = Modifier.size(size))
}