package com.example.agendasmart.network

import com.example.agendasmart.network.ContactService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // URL da API de Contatos
    private const val CONTACTS_URL = "https://apitesteaula.onrender.com/"

    // URL da API do ViaCEP
    private const val VIACEP_URL = "https://viacep.com.br/ws/"

    // Configuração para Contatos
    val contactService: ContactService by lazy {
        Retrofit.Builder()
            .baseUrl(CONTACTS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ContactService::class.java)
    }

    // Configuração para ViaCEP
    val viaCEPService: ViaCEPService by lazy {
        Retrofit.Builder()
            .baseUrl(VIACEP_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViaCEPService::class.java)
    }
}
