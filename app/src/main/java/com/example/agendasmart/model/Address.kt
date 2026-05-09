package com.example.agendasmart.model

data class Address(
    val cep: String,
    val logradouro: String,
    val complemento: String,
    val bairro: String,
    val localidade: String, // Para o ViaCEP, localidade = Cidade
    val uf: String,          // Para o ViaCEP, uf = Estado
    val erro: Boolean = false
)
