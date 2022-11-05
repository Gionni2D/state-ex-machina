package com.gionni2d.mviapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.gionni2d.mvi.compose.rememberMviComponent
import com.gionni2d.mviapp.routes.login.LoginScreen
import com.gionni2d.mviapp.routes.login.LoginViewModel
import com.gionni2d.mviapp.ui.theme.MviTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val model: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MviTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val (stateFlow, onIntent) = rememberMviComponent(model)
                    val state by stateFlow.collectAsState()

                    LoginScreen(
                        state = state,
                        onIntent = onIntent
                    )
                }
            }
        }
    }
}
