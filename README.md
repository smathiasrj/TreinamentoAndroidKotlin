# 🐾 Sistema de Cadastro de Pets

Este projeto faz parte do diretório de estudos **TreinamentoAndroidKotlin**. Trata-se de uma aplicação de console em Kotlin desenvolvida como atividade prática para o curso de **Introdução ao Desenvolvimento com Kotlin**.

## 📌 Sobre o Projeto

O sistema simula o gerenciamento de um mini abrigo de animais. Através de um menu interativo no terminal, o usuário pode realizar operações completas de CRUD (Criar, Ler, Atualizar e Deletar) para os animais resgatados.

**Regra de Negócio Principal:**
Para manter a coesão dos dados, o sistema possui uma trava de segurança "anti-clone". **Não é permitido cadastrar ou alterar o nome de um pet para que ele fique com o mesmo nome de outro pet já existente, a não ser que eles sejam de espécies/raças diferentes** (Exemplo: É permitido ter o "Max" Cachorro e o "Max" Gato, mas nunca dois "Max" Cachorros).

## ✅ Requisitos da Atividade (O que foi implementado)

O projeto foi desenvolvido atendendo a todos os requisitos solicitados:

1. **Classe de Modelo:** Criação da camada de dados com a entidade principal (`Pet`).
2. **Estrutura de Lista:** Uso de uma coleção `MutableList` para armazenar os pets em memória durante a execução.
3. **Funcionalidades (CRUD):**
   - **[A] Cadastrar:** Inserção de novos pets recebendo Nome e Espécie.
   - **[B] Listar:** Exibição amigável de todos os registros contidos no abrigo.
   - **[C] Pesquisar:** Busca *case-insensitive* informando o nome do Pet (retorna todos os pets com aquele nome).
   - **[D] Alterar:** Edição de dados buscando pelo ID único.
   - **[E] Remover:** Exclusão limpa de um registro buscando pelo ID.
   - **[F] Finalizar:** Opção para encerrar o laço de repetição do programa.
4. **Null Safety:** A propriedade `especie` na classe de modelo é do tipo *Nullable* (`String?`), garantindo que o programa não quebre caso a espécie do animal resgatado seja desconhecida.
5. **Elvis Operator (?:):** Utilizado na formatação de impressão e validações. Caso a espécie venha nula, o operador atribui um valor padrão legível (ex: `"Não informada"`).
6. **Orientação a Objetos (POO):** Lógica de negócios isolada e encapsulada na classe `PetManager`, separando o gerenciamento de dados da classe `main`.
7. **Validações de Coesão:** Prevenção contra cadastros sem nome (`isBlank`), proteções com `try-catch` para evitar a quebra do programa ao digitar letras no lugar de números (IDs ou Opções de Menu) e a regra de negócio anti-duplicidade exata.
8. **Interação via Scanner:** Toda a entrada de dados do usuário e interação via terminal ocorre através da classe `java.util.Scanner`.

## 🛠️ Tecnologias Utilizadas
- **Linguagem:** Kotlin
- **Ambiente:** Android Studio
