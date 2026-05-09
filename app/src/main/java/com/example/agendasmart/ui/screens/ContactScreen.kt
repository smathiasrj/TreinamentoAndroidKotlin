package com.example.agendasmart.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.agendasmart.model.Contact
import com.example.agendasmart.ui.components.ContactItem
import com.example.agendasmart.ui.components.ContactTextField
import com.example.agendasmart.ui.components.SectionCard
import com.example.agendasmart.viewmodel.ContactViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Funções de formatação
fun formatPhone(digits: String): String {
    return when {
        digits.length <= 2 -> "(${digits}"
        digits.length <= 7 -> "(${digits.substring(0, 2)}) ${digits.substring(2)}"
        digits.length <= 11 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7)}"
        else -> digits
    }
}

fun formatDate(digits: String): String {
    return when {
        digits.length <= 2 -> digits
        digits.length <= 4 -> "${digits.substring(0, 2)}/${digits.substring(2)}"
        else -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4)}"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(viewModel: ContactViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var birthDate by remember { mutableStateOf(TextFieldValue("")) }
    var cep by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    
    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }

    // Estados para o Calendário (DatePicker)
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Bloqueia datas futuras: o tempo selecionado deve ser menor ou igual ao tempo agora
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // AlertDialog de Confirmação de Exclusão
    if (contactToDelete != null) {
        AlertDialog(
            onDismissRequest = { contactToDelete = null },
            title = { Text("Excluir Contato") },
            text = { Text("Tem certeza que deseja excluir o contato ${contactToDelete?.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        contactToDelete?.id?.let { viewModel.deleteContact(it) }
                        contactToDelete = null
                        scope.launch { snackbarHostState.showSnackbar("✓ Contato excluído") }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { contactToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadContacts()
    }

    val address = viewModel.address
    val cepError = viewModel.cepError
    LaunchedEffect(address, cepError, cep) {
        if (address != null && !cepError) {
            neighborhood = address.bairro
            street = address.logradouro
            city = address.localidade
            state = address.uf
        } else if (cepError || cep.length < 8) {
            // Limpa os campos se o CEP não for encontrado ou estiver incompleto
            neighborhood = ""; street = ""; city = ""; state = ""
        }
    }

    LaunchedEffect(viewModel.saveSuccess) {
        if (viewModel.saveSuccess) {
            snackbarHostState.showSnackbar("✓ Contato salvo com sucesso!")
            name = ""; email = ""; phone = TextFieldValue(""); birthDate = TextFieldValue("")
            cep = ""; street = ""; neighborhood = ""; number = ""; city = ""; state = ""
            viewModel.clearAddress()
            nameError = false; phoneError = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("AgendaSmart", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("Gerenciador de Contatos", style = MaterialTheme.typography.bodySmall)
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    SectionCard(title = "Dados Pessoais", icon = Icons.Default.Person) {
                        ContactTextField(value = name, onValueChange = { name = it; nameError = it.isBlank() }, label = "Nome Completo *", isError = nameError)
                        ContactTextField(value = email, onValueChange = { email = it }, label = "E-mail", keyboardType = KeyboardType.Email)
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { tfv ->
                                val digits = tfv.text.filter { it.isDigit() }.take(11)
                                val formatted = formatPhone(digits)
                                phone = TextFieldValue(text = formatted, selection = TextRange(formatted.length))
                                phoneError = digits.length < 10
                            },
                            label = { Text("Telefone *") },
                            isError = phoneError,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        // Campo de Data de Nascimento (agora abre o calendário)
                        OutlinedTextField(
                            value = birthDate,
                            onValueChange = { }, // Não permite digitar
                            label = { Text("Data de Nascimento") },
                            readOnly = true, // Apenas leitura
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Selecionar Data")
                                }
                            }
                        )

                        // O DIÁLOGO DO CALENDÁRIO
                        if (showDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        val selectedDate = datePickerState.selectedDateMillis
                                        if (selectedDate != null) {
                                            // Converte o tempo (milisegundos) para o formato DD/MM/AAAA
                                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                            val dateString = formatter.format(Date(selectedDate))
                                            birthDate = TextFieldValue(text = dateString)
                                        }
                                        showDatePicker = false
                                    }) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker = false }) {
                                        Text("Cancelar")
                                    }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }
                    }
                }

                item {
                    SectionCard(title = "Endereço", icon = Icons.Default.LocationOn) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.weight(0.6f)) {
                                ContactTextField(
                                    value = cep,
                                    onValueChange = {
                                        cep = it.filter { it.isDigit() }.take(8)
                                        if (cep.length == 8) viewModel.fetchAddress(cep)
                                    },
                                    label = "CEP",
                                    keyboardType = KeyboardType.Number,
                                    isError = viewModel.cepError
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.weight(0.4f)) {
                                ContactTextField(value = number, onValueChange = { number = it.filter { it.isDigit() } }, label = "Nº", keyboardType = KeyboardType.Number)
                            }
                        }
                        if (viewModel.cepError) {
                            Text("CEP não encontrado", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp))
                        }
                        ContactTextField(value = street, onValueChange = { street = it }, label = "Logradouro")
                        ContactTextField(value = neighborhood, onValueChange = { neighborhood = it }, label = "Bairro")
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.weight(0.7f)) { ContactTextField(value = city, onValueChange = { city = it }, label = "Cidade") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.weight(0.3f)) { ContactTextField(value = state, onValueChange = { state = it }, label = "UF") }
                        }
                    }

                    Button(
                        onClick = {
                            val newContact = Contact(
                                name = name, email = email, phone = phone.text, birthDate = birthDate.text,
                                cep = cep, street = street, neighborhood = neighborhood,
                                number = number, city = city, state = state
                            )
                            if (viewModel.validateContact(newContact)) {
                                viewModel.saveContact(newContact)
                            } else {
                                nameError = name.isBlank()
                                phoneError = phone.text.filter { it.isDigit() }.length < 10
                                scope.launch { snackbarHostState.showSnackbar("⚠️ Nome e Telefone são obrigatórios!") }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 8.dp),
                        enabled = !viewModel.isSaving,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        if (viewModel.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onTertiary)
                        } else {
                            Icon(Icons.Default.Done, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Salvar Contato", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
                    Text(text = "Contatos Salvos (${viewModel.contacts.size})", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                }



                if (viewModel.contacts.isEmpty() && viewModel.networkError == null) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Search, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Nenhum contato encontrado", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }

                items(viewModel.contacts) { contact ->
                    ContactItem(contact = contact, onDelete = { contactToDelete = contact })
                }
            }
        }
    }
}
