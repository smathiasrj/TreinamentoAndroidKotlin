package com.example.gestaodespesas

// Esta classe representa uma movimentação financeira, seja ela um ganho ou um gasto.
data class Transacao(
    // O nome ou título da transação que aparecerá na lista.
    val descricao: String,
    // O valor em dinheiro da transação.
    val valor: Double,
    // O período (mês/ano) em que essa transação ocorreu.
    val mes: String
)

// Aqui definimos a estrutura de um "Sonho" ou objetivo financeiro do usuário.
data class Meta(
    // Identificador único gerado automaticamente para cada novo sonho.
    val id: String = java.util.UUID.randomUUID().toString(),
    // O que o usuário deseja conquistar ou realizar.
    val descricao: String,
    // Quanto dinheiro é necessário para atingir esse objetivo.
    val valorNecessario: Double,
    // A data limite ou esperada para realizar esse sonho.
    val prazo: String
)

