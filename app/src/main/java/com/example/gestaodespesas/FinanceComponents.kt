package com.example.gestaodespesas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gestaodespesas.ui.theme.AzulBancarioCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import com.example.gestaodespesas.ui.theme.AzulMarinho
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material.icons.filled.PriorityHigh


// Esta é uma função utilitária para transformar números comuns em formato de moeda brasileira (R$).
fun Double.formatarMoeda(): String {
    // Definimos as regras de formatação do Brasil.
    val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    // Retornamos o número já transformado em texto com o símbolo R$.
    return formatador.format(this)
}


// Este componente cria os títulos das seções nas telas de ganhos, gastos e sonhos.
@Composable
fun FinanceTitle(titulo: String, cor: Color) {
    // Um texto estilizado com tamanho grande e uma cor que combina com o tema da tela.
    Text(
        text = titulo,
        color = cor,
        style = MaterialTheme.typography.titleLarge,
        // Adicionamos um pequeno espaço abaixo do título para não grudar no conteúdo.
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

// Este cartão é usado para dar destaque aos valores principais, como o saldo total ou totais mensais.
@Composable
fun SummaryCard(label: String, valor: Double, corDestaque: Color, pequeno: Boolean = false) {
    // Configuramos o cartão com uma cor de fundo suave baseada na cor de destaque.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = corDestaque.copy(alpha = 0.1f)
        )
    ) {
        // Organizamos o rótulo descritivo e o valor de forma centralizada.
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // O nome do que o cartão está resumindo (ex: "Saldo Geral").
            Text(label, color = Color.Gray, style = MaterialTheme.typography.labelMedium)
            // O valor em dinheiro formatado, que ganha mais destaque visual.
            Text(
                text = valor.formatarMoeda(),
                color = corDestaque,
                style = if (pequeno) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

// Este componente representa um item individual na lista de ganhos ou gastos.
@Composable
fun TransactionCard(
    transacao: Transacao,
    corValor: Color,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    // Criamos um cartão com cor escura padronizada para os itens da lista.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = AzulBancarioCard)
    ) {
        // Colocamos as informações lado a lado: descrição, valor e botões de ação.
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Agrupamos o nome da transação e o período um acima do outro.
            Column(modifier = Modifier.weight(1f)) {
                // A descrição que o usuário deu ao gasto ou ganho.
                Text(
                    transacao.descricao,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                // O mês e ano em que o registro foi feito.
                Text(
                    transacao.mes,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            // Exibimos o valor da transação com a cor definida pelo tipo (Verde ou Vermelho).
            Text(
                text = transacao.valor.formatarMoeda(),
                color = corValor,
                style = MaterialTheme.typography.bodyLarge
            )

            // Um botão discreto para excluir o registro se o usuário desejar.
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir",
                    tint = corValor
                )
            }

            // Um botão para abrir a edição dos dados deste item.
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color.LightGray
                )
            }
        }
    }
}

// Este é o selecionador de data que abre uma gaveta na parte inferior da tela.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearSelector(
    mesSelecionado: String,
    anoSelecionado: String,
    listaAnos: List<String>,
    onDateSelected: (String, String) -> Unit
) {
    // Controlamos se a gaveta (bottom sheet) está visível ou escondida.
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Estados para gerenciar a rolagem dos anos e qual ano está selecionado temporariamente.
    val scrollStateAnos = rememberScrollState()
    var anoTemp by remember { mutableStateOf(anoSelecionado.ifEmpty { listaAnos.getOrElse(0) { "2026" } }) }

    // O campo que o usuário clica para abrir a seleção de período.
    OutlinedTextField(
        value = if (mesSelecionado.isEmpty()) "Selecionar Período" else "$mesSelecionado/$anoSelecionado",
        onValueChange = { },
        readOnly = true,
        label = { Text("Período") },
        trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showSheet = true },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = Color.White,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

    // Aqui montamos o conteúdo da gaveta de seleção.
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = AzulMarinho
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Seção para escolher o ano.
                Text(
                    "Selecione o Ano",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                // Uma linha horizontal onde o usuário desliza para os lados para escolher o ano.
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .horizontalScroll(scrollStateAnos)
                )
                {
                    listaAnos.forEach { ano ->
                        FilterChip(
                            selected = ano == anoTemp,
                            onClick = { anoTemp = ano },
                            label = { Text(ano) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Uma divisória simples no menu.
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.DarkGray
                )

                // Seção para escolher o mês desejado.
                Text(
                    "Selecione o Mês",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                // Uma grade para organizar os 12 meses do ano de forma fácil de clicar.
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .height(250.dp)
                        .padding(vertical = 8.dp)
                ) {
                    items(FinanceConstants.MESES) { mes ->
                        // Um botão para cada mês disponível.
                        Button(
                            onClick = {
                                onDateSelected(
                                    mes,
                                    anoTemp
                                )
                                showSheet = false
                            },
                            modifier = Modifier.padding(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (mes == mesSelecionado) MaterialTheme.colorScheme.primary else Color.DarkGray,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                mes,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

// Este é o formulário principal usado para cadastrar ou editar tanto ganhos quanto gastos.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceForm(
    tituloBotao: String,
    corTematica: Color,
    sugestoes: List<String>,
    transacaoParaEditar: Transacao? = null,
    onSave: (Transacao) -> Unit
) {
    // Guardamos o que o usuário digita nos campos de texto.
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var mesSelecionado by remember { mutableStateOf("") }
    var anoSelecionado by remember { mutableStateOf("") }
    var expandidoSugestao by remember { mutableStateOf(false) }

    // Quando iniciamos uma edição, preenchemos o formulário com os dados antigos automaticamente.
    LaunchedEffect(transacaoParaEditar) {
        transacaoParaEditar?.let {
            descricao = it.descricao
            valor = String.format(Locale("pt", "BR"), "%.2f", it.valor)
            val partes = it.mes.split("/")
            if (partes.size == 2) {
                mesSelecionado = partes[0]
                anoSelecionado = partes[1]
            }
        } ?: run {
            descricao = ""; valor = ""; mesSelecionado = ""; anoSelecionado = ""
        }
    }

    // Verificamos se todos os campos estão preenchidos para habilitar o botão de salvar.
    val camposPreenchidos =
        descricao.isNotBlank() && valor.isNotBlank() && mesSelecionado.isNotBlank()
    // Filtramos a lista de ideias baseada no que o usuário está digitando agora.
    val sugestoesFiltradas = sugestoes.filter { it.contains(descricao, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Campo de texto para a descrição que sugere nomes conhecidos enquanto o usuário digita.
        ExposedDropdownMenuBox(
            expanded = expandidoSugestao && sugestoesFiltradas.isNotEmpty(),
            onExpandedChange = { expandidoSugestao = it }
        ) {
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it; expandidoSugestao = true },
                label = { Text("Descrição") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
            )
            // Menu suspenso com as sugestões de nomes.
            ExposedDropdownMenu(
                expanded = expandidoSugestao && descricao.isNotEmpty(),
                onDismissRequest = { expandidoSugestao = false }
            ) {
                sugestoesFiltradas.forEach { sugestao ->
                    DropdownMenuItem(
                        text = { Text(sugestao) },
                        onClick = { descricao = sugestao; expandidoSugestao = false }
                    )
                }
            }
        }

        // Campo numérico para o valor do lançamento financeiro.
        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor (R$)") },
            placeholder = { Text("0,00") },
            // Mostramos o teclado numérico para facilitar a digitação do dinheiro.
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        // Selecionador de período (Mês/Ano).
        Spacer(modifier = Modifier.height(8.dp))
        MonthYearSelector(
            mesSelecionado = mesSelecionado,
            anoSelecionado = anoSelecionado,
            listaAnos = FinanceConstants.ANOS,
            onDateSelected = { m, a -> mesSelecionado = m; anoSelecionado = a }
        )


        // Botão para salvar o novo registro ou a alteração.
        Button(
            onClick = {
                // Ajustamos o formato do número antes de salvar definitivamente.
                val valorLimpo = valor.trim().replace(".", "").replace(",", ".")
                val valorNumerico = valorLimpo.toDoubleOrNull() ?: 0.0

                onSave(Transacao(descricao, valorNumerico, "$mesSelecionado/$anoSelecionado"))

                // Limpamos o formulário para a próxima entrada de dados.
                descricao = ""; valor = ""; mesSelecionado = ""; anoSelecionado = ""
            },
            enabled = camposPreenchidos,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = corTematica,
                contentColor = Color.White
            )
        ) {
            Text(tituloBotao)
        }
    }
}

// Este cartão exibe um "Sonho" com uma barra de progresso mostrando quanto falta para conquistá-lo.
@Composable
fun MetaCard(
    meta: Meta,
    valorAlocado: Double,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    emoji: String = "",
    podeEditar: Boolean = true
) {
    // Calculamos a porcentagem do objetivo que já foi alcançada.
    val progresso = if (meta.valorNecessario > 0) (valorAlocado / meta.valorNecessario).toFloat() else 0f

    // Definimos uma cor especial para o título se o sonho estiver concluído ou atrasado.
    val corStatus = when (emoji) {
        "CONCLUIDA" -> Color(0xFF2D6A4F) // Verde escuro para metas batidas.
        "ATRASADA" -> Color(0xFFBC4749) // Tom de vermelho para prazos vencidos.
        else -> Color.White // Cor padrão para metas em andamento.
    }

    // Estrutura do cartão que envolve todo o conteúdo do sonho.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = AzulBancarioCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Linha com o título do sonho, o prazo e os ícones de status ou ação.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Informações textuais.
                Column(modifier = Modifier.weight(1f)) {
                    // O nome do sonho (ex: "Carro Novo").
                    Text(
                        text = meta.descricao,
                        color = corStatus,
                        style = MaterialTheme.typography.titleMedium
                    )
                    // O prazo final para realizar este objetivo.
                    Text(
                        "Prazo: ${meta.prazo}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                // Mostramos ícones de status apenas quando o cartão está no Dashboard (resumo).
                if (!podeEditar) {
                    when (emoji) {
                        "CONCLUIDA" -> {
                            // Um check de vitória quando a meta está completa.
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = corStatus,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        "ATRASADA" -> {
                            // Um sinal de atenção para metas com prazo vencido.
                            Icon(
                                Icons.Default.PriorityHigh,
                                contentDescription = null,
                                tint = corStatus,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Botões de editar e excluir, mostrados apenas na tela principal de sonhos.
                if (podeEditar) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = com.example.gestaodespesas.ui.theme.OuroDestaque
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color.LightGray
                        )
                    }
                }
            }

            // Espaço acima da barra.
            Spacer(modifier = Modifier.height(12.dp))

            // A barra de progresso visual que mostra graficamente a conquista.
            LinearProgressIndicator(
                progress = { progresso.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = com.example.gestaodespesas.ui.theme.OuroDestaque,
                trackColor = Color.DarkGray,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            // Pequeno espaço entre a barra e os textos explicativos abaixo.
            Spacer(modifier = Modifier.height(8.dp))

            // Exibimos os valores em dinheiro de quanto já temos e qual é o objetivo final.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Valor que o usuário já conseguiu economizar para este sonho.
                Text(
                    text = "Alocado: ${valorAlocado.formatarMoeda()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = com.example.gestaodespesas.ui.theme.OuroDestaque
                )
                // Valor total necessário para o sonho.
                Text(
                    text = "Meta: ${meta.valorNecessario.formatarMoeda()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        }
    }
}


// Este formulário permite ao usuário cadastrar ou modificar seus sonhos e objetivos financeiros.
@Composable
fun MetaForm(
    tituloBotao: String,
    metaParaEditar: Meta? = null,
    onSave: (Meta) -> Unit
) {
    // Campos para coletar as informações da meta do usuário.
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var mesSelecionado by remember { mutableStateOf("") }
    var anoSelecionado by remember { mutableStateOf("") }
    // Calculamos o ano atual para oferecer opções de prazos futuros no selecionador.
    val anoAtual = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    val anosDosSonhos = (0..5).map { (anoAtual + it).toString() }

    // Preenchemos os campos automaticamente se o usuário estiver editando uma meta existente.
    LaunchedEffect(metaParaEditar) {
        metaParaEditar?.let {
            descricao = it.descricao
            valor = String.format("%.2f", it.valorNecessario)
            val partes = it.prazo.split("/")
            if (partes.size == 2) {
                mesSelecionado = partes[0]
                anoSelecionado = partes[1]
            }
        } ?: run {
            descricao = ""; valor = ""; mesSelecionado = ""; anoSelecionado = ""
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Campo onde o usuário escreve qual é o seu sonho.
        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("O que você quer conquistar?") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo para digitar a meta financeira em Reais.
        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor Necessário (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        // Botão que abre a gaveta de seleção para definir o prazo do sonho.
        Spacer(modifier = Modifier.height(8.dp))
        MonthYearSelector(
            mesSelecionado = mesSelecionado,
            anoSelecionado = anoSelecionado,
            listaAnos = anosDosSonhos,
            onDateSelected = { m, a -> mesSelecionado = m; anoSelecionado = a }
        )

        // Botão de ação para salvar a meta ou as alterações.
        Button(
            onClick = {
                // Transformamos os textos digitados em valores numéricos reais antes de salvar.
                val valorNumerico = valor.replace(",", ".").toDoubleOrNull() ?: 0.0
                onSave(
                    Meta(
                        id = metaParaEditar?.id ?: java.util.UUID.randomUUID().toString(),
                        descricao = descricao,
                        valorNecessario = valorNumerico,
                        prazo = "$mesSelecionado/$anoSelecionado"
                    )
                )
                // Limpamos o formulário após a ação bem-sucedida.
                descricao = ""; valor = ""; mesSelecionado = ""; anoSelecionado = ""
            },
            // O botão só fica ativo quando as informações básicas estão presentes.
            enabled = descricao.isNotBlank() && valor.isNotBlank() && mesSelecionado.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = com.example.gestaodespesas.ui.theme.OuroDestaque,
                contentColor = AzulMarinho
            )
        ) {
            Text(tituloBotao)
        }
    }
}

// Este gráfico em formato de anel mostra visualmente o quanto falta para atingir todos os sonhos cadastrados.
@Composable
fun DonutChart(alocado: Double, meta: Double, valorQueFalta: String) {
    // Gerenciamos o estado para que o gráfico "cresça" na tela quando ela for aberta.
    var progressoAparicao by remember { mutableStateOf(0f) }
    val proporcaoReal = if (meta > 0) (alocado / meta).toFloat() else 0f

    // Disparamos a animação assim que o componente aparece.
    LaunchedEffect(Unit) {
        progressoAparicao = proporcaoReal
    }

    // Configuramos uma animação suave que leva 1.5 segundos para completar o arco.
    val progressoAnimado by androidx.compose.animation.core.animateFloatAsState(
        targetValue = progressoAparicao,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = 1500,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "animacaoDonut"
    )

    // Organizamos o gráfico e o texto no centro da mesma área.
    Box(
        modifier = Modifier.fillMaxWidth().height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // O Canvas é onde "desenhamos" literalmente o gráfico usando formas geométricas.
        Canvas(modifier = Modifier.size(190.dp)) {
            val strokeWidth = 50f

            // Desenha o círculo de fundo em cinza claro (o trilho do gráfico).
            drawArc(
                color = Color.DarkGray.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = strokeStyle(strokeWidth)
            )

            // Desenha o arco de progresso com um gradiente dourado elegante.
            drawArc(
                brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                    colors = listOf(Color(0xFFB8860B), Color(0xFFD4AF37), Color(0xFFB8860B))
                ),
                startAngle = -90f,
                sweepAngle = 360f * progressoAnimado,
                useCenter = false,
                style = strokeStyle(strokeWidth)
            )
        }

        // Colocamos as informações no "miolo" do gráfico para dar contexto aos números.
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Um ícone de estrela que conecta visualmente com a ideia de "Sonhos".
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Star,
                contentDescription = null,
                tint = com.example.gestaodespesas.ui.theme.OuroDestaque,
                modifier = Modifier.size(28.dp).padding(bottom = 4.dp)
            )

            // O rótulo da seção.
            Text(
                "Objetivos",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            // O valor exato que ainda falta para completar o próximo passo.
            Text(
                text = "Falta $valorQueFalta",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            // A porcentagem conquistada exibida em texto.
            val percentual = (progressoAnimado * 100).toInt()
            Text(
                text = "$percentual% conquistado",
                style = MaterialTheme.typography.labelSmall,
                color = com.example.gestaodespesas.ui.theme.OuroDestaque.copy(alpha = 0.9f)
            )
        }
    }
}


// Função auxiliar para definir como o traço do gráfico deve ser desenhado (grossura e pontas arredondadas).
private fun strokeStyle(width: Float) = androidx.compose.ui.graphics.drawscope.Stroke(
    width = width,
    cap = androidx.compose.ui.graphics.StrokeCap.Round
)




