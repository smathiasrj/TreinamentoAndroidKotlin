package com.example.pets
// REQUISITO : Interação via Scanner
// Importamos a classe Scanner para conseguir ler o que o usuário digita no terminal.
import java.util.Scanner

// --- REQUISITO : Classe de Modelo ---
// Aqui eu criei a classe que representa a entidade principal do nosso sistema: o Pet.
// Usei 'data class' porque é a forma ideal do Kotlin para guardar dados simples.
data class Pet(
    val id: Int,// Código fixo do pet, não muda (por isso o 'val')
    var nome: String,// Nome do pet, pode ser alterado depois (por isso 'var')
    var especie: String? = null // Requisito : Null Safety
    // Assim, se o usuário não souber a raça/espécie, o programa não quebra.
) {
    // Essa função (sobreescrita) serve só para deixar a impressão do pet fique amigável.
    // Sem ela, quando mandamos imprimir, aparece aquele código de memória estranho.
    override fun toString(): String {
        // --- REQUISITO : Elvis Operator (?:) ---
        // Se a espécie for nula, o Elvis Operator (?:) coloca o texto "Não informada" no lugar de "null".
        val infoEspecie = especie ?: "Não informada"
        return "ID: $id | Nome: $nome | Espécie: $infoEspecie"
    }
}

// --- REQUISITO : Orientação a Objetos (POO) ---
// Criei essa classe (PetManager) para não deixar os métodos espalhados no 'main'.
// Aqui ficam encapsuladas a nossa lista e as funções que realmente mexem nos dados.
class PetManager {
    // --- REQUISITO : Estrutura de Lista ---
    // Usei MutableList porque a lista precisa crescer e diminuir conforme eu adiciono ou removo pets.
    private val listaPets = mutableListOf<Pet>()
    private var contadorId = 1

    // metodo para verificar se a lista está vazia
    fun estaVazio(): Boolean = listaPets.isEmpty()

    // --- REQUISITO : Cadastrar ---
    // Função que recebe o nome e a espécie lá do 'main' e guarda um novo Pet na lista.
    fun cadastrar(nome: String, especie: String?) {
        // --- REQUISITO : Validações ---
        // Verificando se o usuário não digitou um nome apenas com espaços em branco
        // Mantemos apenas a validação técnica (Coesão)
        if (nome.isBlank()) {
            println("❌ Erro: O pet precisa de um nome para ser cadastrado.")
            return
        }

        val novoPet = Pet(contadorId++, nome, especie)
        listaPets.add(novoPet)
        println("✅ $nome foi adicionado(a) com sucesso!")
    }

    // --- REQUISITO : Listar ---
    // Função que passa por toda a lista e mostra na tela os pets cadastrados.
    fun listar() {
        if (listaPets.isEmpty()) {
            println("🏠 O abrigo está vazio no momento.")
        } else {
            println("\n--- LISTA DE PETS CADASTRADOS ---")
            // o forEach vai rodar por cada item da nossa lista e imprimir usando o toString()
            listaPets.forEach {
                val infoEspecie = it.especie ?: "Não informada"
                println("ID: ${it.id} | Nome: ${it.nome} | Espécie: $infoEspecie")
            }
        }
    }

    // --- REQUISITO : Pesquisar ---
    // Função que recupera os dados de um pet pelo nome.
    // IgnoreCase = true serve para a busca ignorar se a pessoa digitou maiúsculo ou minúsculo.
    fun pesquisar(nomeBusca: String): List<Pet>? {
        val encontrados = listaPets.filter { it.nome.equals(nomeBusca, ignoreCase = true) }
        // Se achou alguém, retorna a lista. Se a lista final ficou vazia, retorna nulo.
        return encontrados.takeIf { it.isNotEmpty() }
    }
    // Função extra que fiz para o 'main' conseguir puxar os dados de um Pet usando o código (ID) dele.
    fun buscarPorId(id: Int): Pet? {
        return listaPets.find { it.id == id }
    }

    // --- REQUISITO 3D: Alterar ---
    // Recebe o ID do pet que a gente quer mudar, junto com os novos dados digitados.
    fun alterar(id: Int, novoNome: String, novaEspecie: String?): Boolean {
        // Primeiro acha o pet na lista
        val petParaAlterar = listaPets.find { it.id == id }
        // Primeiro acha o pet na lista
        if (petParaAlterar == null) return false

        // Validando para não deixar alterar para um nome vazio
        if (novoNome.isBlank()) {
            println("❌ Erro: O nome nao pode ser vazio.")
            return false
        }

        // Se deu tudo certo, atualiza os dados do objeto

        petParaAlterar.nome = novoNome
        petParaAlterar.especie = novaEspecie
        return true
    }

    // --- REQUISITO : Remover ---
    // Apaga um pet da nossa lista baseado no ID dele.
    fun remover(id: Int): Boolean {
        return listaPets.removeIf { it.id == id }
    }
}

// INTERAÇÃO COM O USUÁRIO
fun main() {
    // Tratamento do problema de acentuação no console
    System.setOut(java.io.PrintStream(System.`out`, true, "UTF-8"))
    // Iniciando o nosso Scanner para ler o teclado
    val sc = Scanner(System.`in`, "UTF-8")
    // Instanciando nossa classe (criando o objeto gerenciador)
    val manager = PetManager()
    var opcao: Int = 0

    // --- REQUISITO : Finalizar ---
    do {
        println("\n--- SISTEMA DE CADASTRO DE PETS ---")
        println("1 - Cadastrar Pet")
        println("2 - Listar Todos")
        println("3 - Pesquisar por Nome")
        println("4 - Alterar Nome")
        println("5 - Remover Pet")
        println("6 - Finalizar")
        print("Escolha uma opção: ")

        // --- REQUISITO : Validações com Try-Catch ---

        // Tratamento simples para garantir que a entrada seja um número
        try {
            opcao = sc.nextInt()
            sc.nextLine() // Limpar o buffer
        } catch (e: Exception) {
            println("❌ Entrada inválida! Digite um número de 1 a 6.")
            sc.nextLine()
            // Volta para o menu inicial
            continue
        }

        when (opcao) {
            1 -> {
                print("Nome do Pet: ")
                val nome = sc.nextLine()

                // --- NOVA TRAVA: VALIDAÇÃO IMEDIATA ---
                if (nome.isBlank()) {
                    println("❌ Erro: O pet precisa de um nome para ser cadastrado.")
                    continue // Volta direto para o menu sem perguntar a espécie
                }
                // Busca se esse nome já existe usando a função pesquisar
                val petExistente = manager.pesquisar(nome)?.firstOrNull()

                // Se petExistente não for nulo, quer dizer que já tem alguem com esse nome
                if (petExistente != null) {
                    // Se achou, usamos o Elvis Operator para mostrar a espécie (aula de 05/03)
                    val especieInfo = petExistente.especie ?: "não informada"

                    println("⚠️ Atenção: Já existe um pet chamado '$nome' da espécie '$especieInfo'.")

                    // Confirmar se a pessoa quer criar um repetido
                    var confirma: String
                    while (true) {
                        print("Deseja mesmo cadastrar outro com esse nome? (S/N): ")
                        confirma = sc.nextLine().uppercase() // Converte para maiúsculo para facilitar

                        if (confirma == "S" || confirma == "N") {
                            break // Resposta válida, sai do laço da pergunta
                        } else {
                            println("❌ Opção inválida! Digite apenas 'S' para Sim ou 'N' para Não.")
                        }
                    }

                    // Se o usuário digitou "N", cancelamos o cadastro
                    if (confirma == "N") {
                        println("❌ Cadastro de duplicata cancelado.")
                        continue
                    }
                }
                // ---------------------------------------

                // Se o nome for novo OU se a pessoa confirmou com "S", seguimos aqui:
                print("Espécie (ou tecle Enter para pular): ")
                val espInput = sc.nextLine()
                // Outro lugar usando Null Safety. Se a pessoa apertou só Enter (ficou vazio), passamos "null"
                val especie = if (espInput.isBlank()) null else espInput

                // --- TRAVA ANTI-CLONE (NOME + ESPÉCIE IDENTICOS) ---
                // Não deixo cadastrar se o nome E a espécie forem totalmente idênticos ao que já existe ---

                if (petExistente != null && petExistente.especie.equals(especie, ignoreCase = true)) {
                    val msgEspecie = especie ?: "não informada"
                    println("❌ Erro: Já existe um(a) '$nome' com a espécie '$msgEspecie'.")
                    println("Não é permitido cadastrar dois pets 100% idênticos.")
                    continue
                }
                // Chamada do cadastro
                manager.cadastrar(nome, especie)
            }
            2 -> manager.listar()
            3 -> {
                print("Nome para busca: ")
                val busca = sc.nextLine()
                val resultado = manager.pesquisar(busca) // Agora recebe uma lista ou nulo

                // Requisito : Implementação do Elvis Operator
                // O joinToString junta os pets da lista com uma quebra de linha (\n)
                // Se 'resultado' for nulo, exibe a mensagem de erro após o ?:
                println(resultado?.joinToString("\n") ?: "🔍 Pet não encontrado na lista.")
            }
            4 -> {
                // Caso não possua nada para alterar
                if (manager.estaVazio()) {
                    println("🏠 O abrigo está vazio no momento. Não há nada para alterar!")
                    continue
                }
                // Lista os pets antes de pedir o ID para facilitar a visualização
                manager.listar()
                println("\n--- INICIANDO ALTERAÇÃO ---")

                // --- TRATAMENTO Try-Catch DE ERRO DE DIGITAÇÃO (LETRA NO ID) ---
                val idAlt = try {
                    print("ID do Pet para alterar: ")
                    val idInput = sc.nextInt()
                    sc.nextLine() // Limpa o buffer
                    idInput
                } catch (e: Exception) {
                    println("❌ Erro: O ID deve ser um número inteiro.")
                    sc.nextLine() // LIMPA A LETRA INVÁLIDA DO BUFFER
                    continue // Volta para o menu
                }

                // VALIDAÇÃO DE ID IMEDIATA ---
                val petParaAlterar = manager.buscarPorId(idAlt)
                if (petParaAlterar == null) {
                    println("❌ Erro: ID $idAlt não encontrado.")
                    continue // Volta para o menu imediatamente
                }

                print("Novo nome: ")
                val novoNome = sc.nextLine()

                // --- TRAVA IMEDIATA NO ALTERAR ---
                if (novoNome.isBlank()) {
                    println("❌ Erro: O nome não pode ser vazio.")
                    continue
                }
                // Pet com o mesmo nome mas de outra especie...
                val petComMesmoNome = manager.pesquisar(novoNome)?.firstOrNull()

                // Só avisamos se encontrarmos o nome em OUTRO pet (ID diferente do idAlt)
                if (petComMesmoNome != null && petComMesmoNome.id != idAlt) {
                    val espExistente = petComMesmoNome.especie ?: "não informada"

                    println("⚠️ Aviso: O nome '$novoNome' já é usado pelo pet de ID ${petComMesmoNome.id} (espécie: $espExistente).")

                    // Validar confirmação repetição do nome ---
                    var confirma: String
                    while (true) {
                        print("Deseja realmente usar esse nome repetido? (S/N): ")
                        confirma = sc.nextLine().uppercase()

                        if (confirma == "S" || confirma == "N") {
                            break // Sai do laço se a resposta for válida
                        } else {
                            println("❌ Resposta inválida! Use apenas S ou N.")
                        }
                    }

                    if (confirma == "N") {
                        println("❌ Alteração cancelada.")
                        continue
                    }
                }
                // -------------------------------------------------------

                print("Nova espécie (ou Enter para deixar vazio/nulo): ")
                val espInput = sc.nextLine()
                val novaEspecie = if (espInput.isBlank()) null else espInput

                // --- TRAVA ANTI-CLONE NO ALTERAR (CASE INSENSITIVE) ---
                if (petComMesmoNome != null && petComMesmoNome.id != idAlt && petComMesmoNome.especie.equals(novaEspecie, ignoreCase = true)) {
                    println("❌ Erro: A alteração geraria um pet idêntico ao ID ${petComMesmoNome.id}!")
                    println("Não é permitido ter dois pets com o mesmo nome e mesma espécie.")
                    continue
                }

                // E manda executar a alteração
                if (manager.alterar(idAlt, novoNome, novaEspecie)) {
                    println("✅ Pet atualizado com sucesso!")
                } else {
                    println("❌ Erro: ID $idAlt não encontrado.")
                }
            }
            5 -> {
                // --- GUARDA DE FLUXO ---
                if (manager.estaVazio()) {
                    println("🏠 O abrigo está vazio no momento. Não há nada para remover!")
                    continue // Interrompe aqui e volta para o menu
                }
                // Lista os pets antes de pedir o ID para facilitar a visualização e evitar erros
                manager.listar()
                println("\n--- INICIANDO REMOÇÃO ---")

                // --- TRATAMENTO DE ERRO DE DIGITAÇÃO (LETRA NO ID) ---
                val idRem = try {
                    print("ID do Pet para remover: ")
                    val idInput = sc.nextInt()
                    sc.nextLine() // Limpa o buffer do número
                    idInput
                } catch (e: Exception) {
                    println("❌ Erro: O ID para remoção deve ser um número inteiro.")
                    sc.nextLine() // Limpa o buffer da letra/entrada inválida
                    continue // Volta para o menu inicial
                }

                if (manager.remover(idRem)) {
                    println("🗑️ Registro removido!")
                } else {
                    println("❌ Erro: ID $idRem inexistente.")
                }
            }
            6 -> println("Finalizando o sistema de pets... Lambidas e tchau! 🐶🐱")
            else -> println("⚠️ Opção fora do menu.")
        }
    } while (opcao != 6) // Requisito : Finalizar
}