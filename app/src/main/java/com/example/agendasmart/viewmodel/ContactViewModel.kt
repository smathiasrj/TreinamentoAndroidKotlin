package com.example.agendasmart.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agendasmart.model.Address
import com.example.agendasmart.model.Contact
import com.example.agendasmart.repository.ContactRepository
import kotlinx.coroutines.launch
import android.util.Log

class ContactViewModel : ViewModel() {

    private val repository = ContactRepository()

    var contacts by mutableStateOf<List<Contact>>(emptyList())
        private set

    var address by mutableStateOf<Address?>(null)
        private set

    var cepError by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var saveSuccess by mutableStateOf(false)
        private set

    // Estado para sabermos se houve erro de rede
    var networkError by mutableStateOf<String?>(null)
        private set

    fun loadContacts() {
        viewModelScope.launch {
            try {
                networkError = null
                val result = repository.getContacts()
                // Ordenar alfabeticamente ignorando maiúsculas/minúsculas
                contacts = result.sortedBy { it.name.lowercase() }
                Log.d("AgendaSmart", "Sucesso: ${result.size} contatos")
            } catch (e: Exception) {
                Log.e("AgendaSmart", "Erro de conexão", e)
                networkError = "Erro de conexão: ${e.localizedMessage}"
            }
        }
    }

    fun saveContact(contact: Contact) {
        viewModelScope.launch {
            isSaving = true
            saveSuccess = false
            try {
                repository.addContact(contact)
                loadContacts()
                saveSuccess = true
            } catch (e: Exception) {
                Log.e("AgendaSmart", "Erro ao salvar", e)
                saveSuccess = false
            } finally {
                isSaving = false
            }
        }
    }

    fun deleteContact(id: Int) {
        viewModelScope.launch {
            try {
                repository.removeContact(id)
                loadContacts()
            } catch (e: Exception) {
                Log.e("AgendaSmart", "Erro ao deletar", e)
            }
        }
    }

    fun fetchAddress(cep: String) {
        if (cep.length == 8) {
            viewModelScope.launch {
                try {
                    val result = repository.getAddress(cep)
                    if (result.erro) {
                        address = null
                        cepError = true
                    } else {
                        address = result
                        cepError = false
                    }
                } catch (e: Exception) {
                    address = null
                    cepError = true
                    Log.e("AgendaSmart", "Erro no CEP", e)
                }
            }
        } else {
            cepError = false
        }
    }

    fun validateContact(contact: Contact): Boolean {
        return contact.name.isNotBlank() && contact.phone.filter { it.isDigit() }.length >= 10
    }

    fun clearAddress() {
        address = null
    }
}
