package com.gionni2d.mviapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingButton(
    text: String,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        content = {
            if (!loading) Text(text = text)
            else CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colors.onPrimary,
                strokeWidth = 2.dp
            )
        },
        modifier = modifier,
        onClick = onClick
    )
}