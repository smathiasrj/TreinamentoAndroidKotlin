package com.example.agendasmart.model

data class Contact(
    val id: Int? = null,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val birthDate: String = "",
    val cep: String = "",
    val neighborhood: String = "",
    val street: String = "",
    val number: String = "",
    val state: String = "",
    val city: String = ""
)
