package com.example.gestaodespesas

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import java.util.Calendar

// Esta classe funciona como o "cérebro" do aplicativo, onde toda a lógica e os dados são gerenciados.
class FinanceViewModel : ViewModel() {

    // Listas internas que guardam as informações de ganhos, gastos e sonhos.
    private val _ganhos = mutableStateListOf<Transacao>()
    private val _gastos = mutableStateListOf<Transacao>()
    private val _sonhos = mutableStateListOf<Meta>()

    // Disponibiliza a lista de ganhos sempre organizada pela data mais recente.
    val ganhos: List<Transacao>
        get() = _ganhos.sortedByDescending { formatarDataParaSorteio(it.mes) }

    // Disponibiliza a lista de gastos sempre organizada pela data mais recente.
    val gastos: List<Transacao>
        get() = _gastos.sortedByDescending { formatarDataParaSorteio(it.mes) }
    
    // Disponibiliza a lista de sonhos organizada por quem tem o prazo mais curto.
    val sonhos: List<Meta>
        get() = _sonhos.sortedBy { formatarDataParaSorteio(it.prazo) }


    // Calcula a soma total de todos os ganhos cadastrados.
    val totalGanhos by derivedStateOf {
        _ganhos.sumOf { it.valor }
    }
    
    // Calcula a soma total de todos os gastos cadastrados.
    val totalGastos by derivedStateOf {
        _gastos.sumOf { it.valor }
    }
    
    // O saldo disponível, subtraindo os gastos do total de ganhos.
    val saldoTotal by derivedStateOf {
        totalGanhos - totalGastos
    }

    // Identifica qual é o próximo sonho da lista que ainda não foi totalmente financiado.
    val proximoSonho by derivedStateOf {
        val ordenados = _sonhos.sortedBy { formatarDataParaSorteio(it.prazo) }
        ordenados.firstOrNull { descobrirValorAlocadoParaMeta(it) < it.valorNecessario }
    }
    
    // Calcula o valor total que já foi separado para todos os sonhos.
    val totalReservaSonhos by derivedStateOf {
        _sonhos.sumOf { descobrirValorAlocadoParaMeta(it) }
    }
    
    // Soma o valor total necessário para realizar todos os sonhos da lista.
    val totalNecessarioSonhos: Double get() = sonhos.sumOf { it.valorNecessario }

    // Soma quanto dinheiro o usuário já "reservou" na prática para seus objetivos.
    val totalAlocadoSonhos: Double get() = sonhos.sumOf { descobrirValorAlocadoParaMeta(it) }

    // Calcula quanto ainda falta economizar para bater todas as metas de sonhos.
    val valorQueFaltaSonhos: Double get() = (totalNecessarioSonhos - totalAlocadoSonhos).coerceAtLeast(0.0)


    // Descobre o mês e ano atuais para sabermos se um sonho está com o prazo vencido.
    private val fatorDataAtual: Int
        get() {
            val calendar = Calendar.getInstance()
            val ano = calendar.get(Calendar.YEAR)
            val mes = calendar.get(Calendar.MONTH) + 1
            return (ano * 100) + mes
        }

    // Filtra e prepara os primeiros sonhos que aparecerão no resumo da tela inicial.
    val proximosSonhosDashboard by derivedStateOf {
        _sonhos.sortedBy { formatarDataParaSorteio(it.prazo) }.take(3)
    }



    // Adiciona um novo registro de ganho na lista.
    fun adicionarGanho(transacao: Transacao) {
        _ganhos.add(transacao)
    }

    // Remove um registro de ganho da lista.
    fun removerGanho(transacao: Transacao) {
        _ganhos.remove(transacao)
    }

    // Substitui um ganho antigo por um novo após uma edição.
    fun atualizarGanho(antigo: Transacao, novo: Transacao) {
        val index = _ganhos.indexOf(antigo)
        if (index != -1) _ganhos[index] = novo
    }

    // Adiciona um novo registro de gasto na lista.
    fun adicionarGasto(transacao: Transacao) {
        _gastos.add(transacao)
    }

    // Remove um registro de gasto da lista.
    fun removerGasto(transacao: Transacao) {
        _gastos.remove(transacao)
    }

    // Substitui um gasto antigo por um novo após uma edição.
    fun atualizarGasto(antigo: Transacao, novo: Transacao) {
        val index = _gastos.indexOf(antigo)
        if (index != -1) _gastos[index] = novo
    }

    // Adiciona uma nova meta de sonho na lista de objetivos.
    fun adicionarMeta(meta: Meta) {
        _sonhos.add(meta)
    }

    // Remove um sonho da lista.
    fun removerMeta(meta: Meta) {
        _sonhos.remove(meta)
    }

    // Atualiza os dados de um sonho que já existia.
    fun atualizarMeta(antiga: Meta, nova: Meta) {
        val index = _sonhos.indexOfFirst { it.id == antiga.id }
        if (index != -1) _sonhos[index] = nova
    }

    // Converte o texto de mês/ano em um número que o computador consegue ordenar cronologicamente.
    private fun formatarDataParaSorteio(dataTexto: String): Int {
        val partes = dataTexto.split("/")
        if (partes.size != 2) return 0
        val mesNome = partes[0]
        val ano = partes[1].toIntOrNull() ?: 0
        val mesIndex = FinanceConstants.MESES.indexOf(mesNome) + 1
        return (ano * 100) + mesIndex
    }

    // Calcula quanto do saldo atual "sobra" para uma meta específica, seguindo a ordem de prioridade.
    fun descobrirValorAlocadoParaMeta(metaAlvo: Meta): Double {
        val metasOrdenadas = _sonhos.sortedBy { formatarDataParaSorteio(it.prazo) }
        var saldoRestante = saldoTotal
        for (meta in metasOrdenadas) {
            // Verifica quanto falta para esta meta específica ser completada.
            val quantoFalta = meta.valorNecessario
            // Distribui o saldo disponível: se houver saldo, preenche a meta; se não, fica zero.
            val valorQueEstaMetaPega =
                if (saldoRestante >= quantoFalta) quantoFalta else maxOf(0.0, saldoRestante)

            // Se chegamos na meta que queríamos consultar, devolvemos o valor calculado para ela.
            if (meta.id == metaAlvo.id) return valorQueEstaMetaPega

            // O que sobrou do saldo vai para a próxima meta da lista.
            saldoRestante -= valorQueEstaMetaPega
        }
        return 0.0
    }

    // Define qual status visual ou ícone um sonho deve exibir dependendo do progresso financeiro e da data.
    fun obterEmojiParaMeta(meta: Meta): String {
        // Busca quanto dinheiro já foi garantido para este sonho.
        val valorAlocado = descobrirValorAlocadoParaMeta(meta)
        // Compara a data da meta com a data de hoje.
        val dataDestaMeta = formatarDataParaSorteio(meta.prazo)

        return when {
            // Se já tem o dinheiro todo, a meta está concluída.
            valorAlocado >= meta.valorNecessario -> "CONCLUIDA"
            // Se a data já passou e o dinheiro não está lá, está atrasada.
            dataDestaMeta <= fatorDataAtual -> "ATRASADA"
            // Caso contrário, é um plano para o futuro.
            else -> "PLANEJADA"
        }
    }
}


