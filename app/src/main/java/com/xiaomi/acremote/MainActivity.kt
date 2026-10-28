package com.xiaomi.acremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.xiaomi.acremote.ui.screens.MainRemoteScreen
import com.xiaomi.acremote.ui.theme.DarkBackground
import com.xiaomi.acremote.ui.theme.MiACRemoteTheme
import com.xiaomi.acremote.ui.viewmodel.RemoteViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RemoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MiACRemoteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    MainRemoteScreen(viewModel = viewModel)
                }
            }
        }
    }
}
