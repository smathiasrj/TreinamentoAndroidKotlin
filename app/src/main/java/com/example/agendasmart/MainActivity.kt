package com.example.agendasmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.agendasmart.ui.screens.ContactScreen
import com.example.agendasmart.ui.theme.AgendaSmartTheme // Verifique se o nome do tema está correto
import com.example.agendasmart.viewmodel.ContactViewModel

class MainActivity : ComponentActivity() {

    // Instancia a ViewModel usando o padrão do Android
    private val viewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita o modo tela cheia
        enableEdgeToEdge()

        setContent {
            // Aplica o tema do seu projeto
            AgendaSmartTheme {
                // Chama a tela principal passando a ViewModel
                ContactScreen(viewModel = viewModel)
            }
        }
    }
}
