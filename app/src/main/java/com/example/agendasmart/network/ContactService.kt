package com.example.agendasmart.network

import com.example.agendasmart.model.Contact
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ContactService {

    // Buscar todos os contatos
    @GET("contacts")
    suspend fun getContacts(): List<Contact>

    // Cadastrar um novo contato
    @POST("contacts")
    suspend fun addContact(@Body contact: Contact): Contact

    // Remover um contato pelo ID
    @DELETE("contacts/{id}")
    suspend fun removeContact(@Path("id") id: Int)

    // Atualizar um contato existente
    @PUT("contacts/{id}")
    suspend fun updateContact(@Path("id") id: Int, @Body contact: Contact): Contact
}
