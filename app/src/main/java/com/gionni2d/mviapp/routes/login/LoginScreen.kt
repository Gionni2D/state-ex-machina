package com.gionni2d.mviapp.routes.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gionni2d.mviapp.R
import com.gionni2d.mviapp.ui.components.LoadingButton
import com.gionni2d.mviapp.ui.components.PasswordTextField
import com.gionni2d.mviapp.ui.components.Spacer4
import com.gionni2d.mviapp.ui.theme.MviTheme

@Composable
fun LoginScreen(
    state: LoginViewState,
    onIntent: (LoginIntent) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (state.showFields) {
            TextField(
                value = state.username,
                label = { Text(text = stringResource(R.string.login_field_username_label)) },
                onValueChange = { LoginIntent.TypeUsername(it).let(onIntent) }
            )
            Spacer4()
            PasswordTextField(
                value = state.password,
                label = { Text(text = stringResource(R.string.login_field_password_label)) },
                onValueChange = { LoginIntent.TypePassword(it).let(onIntent) }
            )
            Spacer4()
        }
        state.errorMessage?.let {
            Text(
                text = it.toLocalizedString(),
                color = MaterialTheme.colors.error
            )
            Spacer4()
        }
        LoadingButton(
            text = state.buttonText.toLocalizedString(),
            loading = state.buttonLoading,
            onClick = {
                val intent = state.buttonIntent
                intent.let(onIntent)
            }
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    val state = LoginState(
        username = "mario.rossi",
        password = "ABC12345678xyz"
    )
    MviTheme {
        LoginScreen(
            state = state,
            onIntent = {}
        )
    }
}