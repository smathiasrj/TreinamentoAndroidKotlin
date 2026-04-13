package com.example.gestaodespesas

// Centralizamos aqui as informações fixas e sugestões que o aplicativo utiliza em várias telas.
object FinanceConstants {
    // Lista com todos os meses do ano para facilitar a seleção pelo usuário.
    val MESES = listOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
    // Os anos disponíveis para o histórico de lançamentos.
    val ANOS = listOf("2024", "2025", "2026")

    // Sugestões comuns que aparecem quando o usuário começa a digitar um novo ganho.
    val SUGESTOES_GANHOS = listOf("Salário", "Freelance", "PIX Recebido", "Venda de Item", "Reembolso", "Rendimento")
    // Lista de gastos frequentes para agilizar o cadastro de despesas.
    val SUGESTOES_GASTOS = listOf("Aluguel", "Energia", "Água", "Internet", "Mercado", "Cartão de Crédito", "Lazer")
}
