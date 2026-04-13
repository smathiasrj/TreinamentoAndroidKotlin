package com.example.gestaodespesas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestaodespesas.ui.theme.AzulBancarioCard
import com.example.gestaodespesas.ui.theme.CoralGasto
import com.example.gestaodespesas.ui.theme.GestaoDespesasTheme
import com.example.gestaodespesas.ui.theme.OuroDestaque
import com.example.gestaodespesas.ui.theme.VerdeLucro
import com.example.gestaodespesas.ui.theme.VermelhoGasto


// Este é o ponto de partida do aplicativo no Android. É aqui que tudo começa a ser desenhado.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val financeViewModel: FinanceViewModel = viewModel()
            GestaoDespesasTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TelaPrincipal(financeViewModel)
                }
            }
        }

    }
}

// Roteiro de navegação: quais telas existem e qual aparece primeiro.
@Composable
fun AppNavigation(navController: NavHostController, viewModel: FinanceViewModel) {
    // O NavHost funciona como um "mestre de cerimônias" que troca as telas conforme clicamos.
    NavHost(
        navController = navController,
        startDestination = "dashboard" // O aplicativo sempre abre no Dashboard (Resumo).
    ) {
        // Cada comando 'composable' associa um nome de destino a uma função que desenha a tela.
        composable("dashboard") { TelaDashboard(viewModel) }
        composable("ganhos") { TelaGanhos(viewModel) }
        composable("gastos") { TelaGastos(viewModel) }
        composable("sonhos") { TelaSonhos(viewModel) }
    }
}


// Esta é a moldura externa do app, que contém a barra de navegação inferior e o espaço para as telas.
@Composable
fun TelaPrincipal(viewModel: FinanceViewModel) {
    // Controlador que vai orquestrar a troca de telas no app.
    val navController = rememberNavController()

    // Organiza barras, menus e o conteúdo.
    Scaffold(
        // Desenhamos a barra inferior com os botões principais de acesso.
        bottomBar = {
            // A NavigationBar é o container que segura os ícones de navegação.
            NavigationBar(
                containerColor = AzulBancarioCard
            ) {
                // Cada Item representa um botão na barra de baixo.
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("dashboard") },
                    label = { Text("Home", color = Color.White) },
                    icon = {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = null, // Ícone da casinha para o Resumo.
                            tint = Color.White
                        )
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("ganhos") },
                    label = { Text("Ganhos", color = Color.White) },
                    icon = {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null, // Ícone de mais (+) para novas entradas.
                            tint = Color(0xFF2D6A4F)
                        )
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("gastos") },
                    label = { Text("Gastos", color = Color.White) },
                    icon = {
                        Icon(
                            Icons.Filled.ShoppingCart,
                            contentDescription = null, // Ícone de carrinho para compras e despesas.
                            tint = Color(0xFFBC4749)
                        )
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("sonhos") },
                    label = { Text("Sonhos", color = Color.White) },
                    icon = {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null, // Ícone de estrela para as metas futuras.
                            tint = Color(0xFFD4AF37)
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        // Este Box garante que o conteúdo das telas não fique "preso" atrás da barra inferior.
        Box(modifier = Modifier.padding(innerPadding)) {
            // Aqui é onde o conteúdo das telas (dashboard, ganhos, etc) realmente aparece.
            AppNavigation(navController, viewModel)
        }
    }
}


// A tela de Resumo (Dashboard) apresenta uma visão geral das finanças e o progresso dos sonhos.
@Composable
fun TelaDashboard(viewModel: FinanceViewModel) {
    val scrollState = rememberScrollState()

    // Organizamos o conteúdo em uma coluna vertical que permite rolar a tela.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Exibimos o título que identifica esta tela para o usuário.
        FinanceTitle("Resumo Financeiro", Color.White)

        // Este cartão centraliza e destaca o saldo total disponível para gastos ou investimentos.
        SummaryCard(
            label = "Saldo Geral Disponível",
            valor = viewModel.saldoTotal,
            corDestaque = OuroDestaque
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Colocamos os totais de ganhos e gastos lado a lado para comparação rápida.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                // Cartão menor que resume tudo o que entrou de dinheiro.
                SummaryCard("Recebido", viewModel.totalGanhos, Color(0xFF2D6A4F), pequeno = true)
            }
            Box(modifier = Modifier.weight(1f)) {
                // Cartão menor que resume tudo o que saiu de dinheiro.
                SummaryCard("Pago", viewModel.totalGastos, Color(0xFFBC4749), pequeno = true)
            }
        }


        // Colocamos uma linha decorativa para separar visualmente as seções.
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = Color.DarkGray)
        Spacer(modifier = Modifier.height(24.dp))

        // Chamamos o gráfico animado que mostra o progresso rumo à realização dos sonhos.
        DonutChart(
            alocado = viewModel.totalAlocadoSonhos,
            meta = viewModel.totalNecessarioSonhos,
            valorQueFalta = viewModel.valorQueFaltaSonhos.formatarMoeda()
        )

        Spacer(modifier = Modifier.height(24.dp))


        // Cartão que mostra o valor total guardado para os seus planos futuros.
        SummaryCard(
            label = "Fundo de Reserva dos Sonhos",
            valor = viewModel.totalReservaSonhos,
            corDestaque = OuroDestaque
        )


        Spacer(modifier = Modifier.height(24.dp))

        // Criamos uma lista com os 3 sonhos que estão mais próximos da conquista.
        val listaSonhos = viewModel.proximosSonhosDashboard

        if (listaSonhos.isNotEmpty()) {
            FinanceTitle("Próximos Sonhos", Color.White)

            listaSonhos.forEach { sonho ->
                val emoji = viewModel.obterEmojiParaMeta(sonho)
                MetaCard(
                    meta = sonho,
                    valorAlocado = viewModel.descobrirValorAlocadoParaMeta(sonho),
                    onDelete = { viewModel.removerMeta(sonho) },
                    onEdit = { /* ... */ },
                    emoji = emoji,
                    podeEditar = false
                )
            }
        } else {
            Text(
                "Cadastre seus sonhos clicando no ícone da ⭐ (Estrela) logo abaixo!",
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )
        }
    }
}


// Tela dedicada para cadastrar todos os seus ganhos e receitas.
@Composable
fun TelaGanhos(viewModel: FinanceViewModel) {
    var ganhoSendoEditado by remember { mutableStateOf<Transacao?>(null) }
    val totalGanhos = viewModel.ganhos.sumOf { it.valor }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FinanceTitle("💰 Ganhos (${viewModel.ganhos.size})", VerdeLucro)
        SummaryCard("Total Recebido", totalGanhos, VerdeLucro)

        // Apresentamos o formulário onde o usuário pode digitar e salvar seus ganhos.
        FinanceForm(
            tituloBotao = if (ganhoSendoEditado == null) "Salvar Ganho" else "Atualizar Ganho",
            corTematica = VerdeLucro,
            sugestoes = FinanceConstants.SUGESTOES_GANHOS,
            transacaoParaEditar = ganhoSendoEditado,
            onSave = { novaTransacao ->
                if (ganhoSendoEditado == null) {
                    viewModel.adicionarGanho(novaTransacao)
                } else {
                    viewModel.atualizarGanho(ganhoSendoEditado!!, novaTransacao)
                    ganhoSendoEditado = null
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Meus Recebimentos", style = MaterialTheme.typography.titleMedium, color = Color.White)

        // Criamos uma lista eficiente para exibir cada ganho já cadastrado.
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.ganhos) { ganho ->
                // Cada item da lista é exibido em um cartão com o valor em destaque.
                TransactionCard(
                    transacao = ganho,
                    corValor = VerdeLucro,
                    onDelete = { viewModel.removerGanho(ganho) },
                    onEdit = { ganhoSendoEditado = ganho }
                )
            }
        }
    }
}

// Tela dedicada para o controle rigoroso de todas as saídas de dinheiro.
@Composable
fun TelaGastos(viewModel: FinanceViewModel) {
    var gastoSendoEditado by remember { mutableStateOf<Transacao?>(null) }
    val totalGastos = viewModel.gastos.sumOf { it.valor }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FinanceTitle("💸 Gastos (${viewModel.gastos.size})", CoralGasto)
        SummaryCard("Total Pago", totalGastos, VermelhoGasto)

        // Formulário inteligente para cadastrar novas despesas.
        FinanceForm(
            tituloBotao = if (gastoSendoEditado == null) "Salvar Gasto" else "Atualizar Gasto",
            corTematica = CoralGasto,
            sugestoes = FinanceConstants.SUGESTOES_GASTOS,
            transacaoParaEditar = gastoSendoEditado,
            onSave = { novaTransacao ->
                if (gastoSendoEditado == null) {
                    viewModel.adicionarGasto(novaTransacao)
                } else {
                    viewModel.atualizarGasto(gastoSendoEditado!!, novaTransacao)
                    gastoSendoEditado = null
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Meus Pagamentos", style = MaterialTheme.typography.titleMedium, color = Color.White)

        // Lista de gastos realizados, permitindo ver cada item individualmente.
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.gastos) { gasto ->
                // Cada gasto é exibido em um cartão com o valor destacado em vermelho.
                TransactionCard(
                    transacao = gasto,
                    corValor = VermelhoGasto,
                    onDelete = { viewModel.removerGasto(gasto) },
                    onEdit = { gastoSendoEditado = gasto }
                )
            }
        }
    }
}


// Tela onde o usuário gerencia seus objetivos de longo prazo (Sonhos).
@Composable
fun TelaSonhos(viewModel: FinanceViewModel) {
    var metaSendoEditada by remember { mutableStateOf<Meta?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FinanceTitle("Meus Sonhos", OuroDestaque)

        SummaryCard(
            label = "Total das Metas",
            valor = viewModel.sonhos.sumOf { it.valorNecessario },
            corDestaque = OuroDestaque
        )

        // Mostramos o formulário específico para criar ou editar os sonhos.
        MetaForm(
            tituloBotao = if (metaSendoEditada == null) "Adicionar Sonho" else "Salvar Alteração",
            metaParaEditar = metaSendoEditada,
            onSave = { novaMeta ->
                if (metaSendoEditada == null) {
                    viewModel.adicionarMeta(novaMeta)
                } else {
                    viewModel.atualizarMeta(metaSendoEditada!!, novaMeta)
                }
                metaSendoEditada = null
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))

        // Exibimos todos os seus sonhos em uma lista vertical com o progresso atualizado.
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(viewModel.sonhos) { meta ->
                // Cada sonho ganha um cartão visual mostrando quanto já foi poupado.
                MetaCard(
                    meta = meta,
                    valorAlocado = viewModel.descobrirValorAlocadoParaMeta(meta),
                    onDelete = { viewModel.removerMeta(meta) },
                    onEdit = { metaSendoEditada = meta }
                )
            }
        }
    }
}
