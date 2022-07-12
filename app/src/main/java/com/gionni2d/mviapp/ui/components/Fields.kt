package com.gionni2d.mviapp.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun PasswordTextField(
    value: String,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    isPasswordVisible: Boolean = false,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        modifier = modifier,
        label = label,
        visualTransformation = if (!isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        onValueChange = onValueChange
    )
}