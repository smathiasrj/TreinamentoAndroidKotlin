package com.example.agendasmart.network // Lembre de ajustar o package se necessário

import com.example.agendasmart.model.Address
import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCEPService {

    // Buscar o endereço pelo CEP
    // O {cep} vai ser substituído pelo número que o usuário digitar
    @GET("{cep}/json/")
    suspend fun getAddress(@Path("cep") cep: String): Address
}
