package com.example.agendasmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.agendasmart.ui.screens.ContactScreen
import com.example.agendasmart.ui.theme.AgendaSmartTheme
import com.example.agendasmart.viewmodel.ContactViewModel

class MainActivity : ComponentActivity() {

    // Instancia a ViewModel
    private val viewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita o modo tela cheia
        enableEdgeToEdge()

        setContent {
            AgendaSmartTheme {
                ContactScreen(viewModel = viewModel)
            }
        }
    }
}
