package com.example.agendasmart.repository

import com.example.agendasmart.model.Address
import com.example.agendasmart.model.Contact
import com.example.agendasmart.network.RetrofitClient

class ContactRepository {

    // Função para buscar todos os contatos da API
    suspend fun getContacts(): List<Contact> {
        return RetrofitClient.contactService.getContacts()
    }

    // Função para cadastrar um novo contato
    suspend fun addContact(contact: Contact): Contact {
        return RetrofitClient.contactService.addContact(contact)
    }

    // Função para remover um contato pelo ID
    suspend fun removeContact(id: Int) {
        RetrofitClient.contactService.removeContact(id)
    }

    // Função para atualizar um contato existente
    suspend fun updateContact(id: Int, contact: Contact): Contact {
        return RetrofitClient.contactService.updateContact(id, contact)
    }

    // Função para buscar o endereço pelo CEP usando a API do ViaCEP
    suspend fun getAddress(cep: String): Address {
        return RetrofitClient.viaCEPService.getAddress(cep)
    }
}
