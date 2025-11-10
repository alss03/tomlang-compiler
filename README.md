# TomLang Compilador

Um compilador e intérprete completo para a linguagem **TomLang**, uma linguagem imperativa simples desenvolvida com **ANTLR 4** e **Java**. O projeto implementa as três fases clássicas da compilação: análise léxica, sintática, semântica e interpretação/execução.

---

## Sobre o Projeto

TomLang é uma linguagem educacional que demonstra:
- ✅ **Análise Léxica e Sintática** via ANTLR 4.13.2
- ✅ **Verificação Semântica** com tabela de símbolos e scopes
- ✅ **Interpretação/Execução** em tempo real via AST visitor
- ✅ **Tratamento de Erros** com mensagens detalhadas por linha
- ✅ **Entrada/Saída** com `scanf` e `printf`

### Pipeline de Compilação

```
Código-fonte (.mlg)
        ↓
    LEXER (Tokenização)
        ↓
    PARSER (Análise Sintática → AST)
        ↓
    SEMANTIC ANALYZER (Verificação de tipos)
        ↓
    INTERPRETER (Execução)
```

---

## Características da Linguagem

### Tipos de Dados
- `int` — Números inteiros (32-bit)
- `float` — Números em ponto flutuante
- `string` — Cadeias de texto entre aspas duplas

### Estruturas de Controle
| Estrutura | Descrição |
|-----------|-----------|
| `if-else` | Condicional com ramo alternativo |
| `while` | Loop com verificação no início |
| `do-while` | Loop com verificação no final |
| `for` | Loop com inicialização, condição e atualização |
| `switch-case` | Seleção múltipla com `default` |
| `break` | Salta para fora de loops/switches |

### Operadores (Por Precedência)
- **Lógico**: `!` (maior precedência), `&&`, `||`
- **Comparação**: `==`, `!=`
- **Relacional**: `<`, `>`, `<=`, `>=`
- **Aritmético**: `+`, `-` (adição/subtração), `*`, `/` (menor precedência)
- **Unário**: `+`, `-`, `!`

### Operações Built-in
- `printf(valor)` — Imprime na saída padrão (int, float, string ou expressões)
- `scanf(variável)` — Lê entrada do usuário e atribui à variável

### Exemplo Rápido
```tomlang
int idade = 25;
if (idade >= 18) {
  printf("Maior de idade");
} else {
  printf("Menor de idade");
}
```

---

## Estrutura do Projeto

```
compilador/
├── README.md                      # Este arquivo (ATUALIZADO)
├── build.gradle                   # Configuração Gradle
├── compilador.iml                 # Configuração IntelliJ IDEA
├── settings.gradle                # Definições Gradle
├── gradlew / gradlew.bat          # Gradle Wrapper (Linux/macOS e Windows)
│
├── grammar/
│   └── TomLang.g4                 # Gramática ANTLR 4
│
├── src/
│   ├── TomLangMain.java           # Ponto de entrada (main)
│   ├── TomLangLexer.java          # Gerado pelo ANTLR (lexer)
│   ├── TomLangParser.java         # Gerado pelo ANTLR (parser)
│   ├── TomLangBaseVisitor.java    # Gerado pelo ANTLR (visitor base)
│   ├── TomLangVisitor.java        # Gerado pelo ANTLR (visitor interface)
│   │
│   ├── SemanticVisitor.java       # Análise semântica e verificação de tipos
│   ├── InterpreterVisitor.java    # Execução/interpretação da AST
│   ├── SymbolTable.java           # Gerenciamento de variáveis e tipos
│   ├── TomLangErrorListener.java  # Coleta e formatação de erros sintáticos
│   │
│   └── [Outros arquivos lexer/parser gerados]
│
├── tests/
│   ├── ok.mlg                     # Exemplo válido
│   └── error.mlg                  # Exemplo com erros
│
└── build/
    └── [Saída da compilação]
```

---

## Instalação e Configuração

### Pré-requisitos
- **Java 21** (compilador suporta Java 21)
- **Git** (opcional, para clonar o repositório)
- **Gradle** (já incluído via Gradle Wrapper — `gradlew` / `gradlew.bat`)

### Verificar Java
```powershell
java -version
```

Deve retornar Java 21 ou superior.

### Clonar o Repositório (Opcional)
```powershell
git clone https://github.com/alss03/tomlang-compiler.git
cd compilador
```

---

## Build e Compilação

### Opção 1: Usar Gradle Wrapper (Recomendado)

### Gerar Código do ANTLR
```powershell
# Windows
.\gradlew.bat generateGrammarSource

# Linux/macOS
./gradlew generateGrammarSource
```

### Compilar o Projeto
```powershell
# Windows
.\gradlew.bat build

# Linux/macOS
./gradlew build
```

### Verificar Build
Se bem-sucedido, verá:
```
BUILD SUCCESSFUL in X.XXs
```

Os arquivos compilados estarão em `build/classes/java/main/`.

### Opção 2: Compilação Manual com Javac

Se preferir compilar manualmente sem Gradle:

### Gerar Código ANTLR
Você precisa de ANTLR 4.13.2. Baixe em: https://www.antlr.org/download/antlr-4.13.2-complete.jar

```powershell
# Crie um diretório para a compilação
mkdir bin

# Gere o código ANTLR
java -jar antlr-4.13.2-complete.jar -visitor -no-listener grammar/TomLang.g4 -o src/
```

### Compilar Tudo
```powershell
javac -cp ".;antlr-4.13.2-complete.jar" -d bin src/*.java
```

---

## Executando Programas

### Opção 1: Com Gradle (Recomendado)

```powershell
# Windows
.\gradlew.bat run --args "tests\ok.mlg"

# Linux/macOS
./gradlew run --args "tests/ok.mlg"
```

### Opção 2: Com Java Direto

Após compilar com Gradle:

```powershell
# Windows (do diretório do projeto)
java -cp "build\classes\java\main;antlr-4.13.2-complete.jar" TomLangMain tests\ok.mlg

# Linux/macOS
java -cp "build/classes/java/main:antlr-4.13.2-complete.jar" TomLangMain tests/ok.mlg
```

---

## Escrevendo Programas TomLang

### Exemplo 1: Hello World Interativo
Crie `tests/hello.mlg`:
```tomlang
string nome;
printf("Qual é seu nome? ");
scanf(nome);
printf("Olá, " + nome);
```

Execute:
```powershell
.\gradlew.bat run --args "tests\hello.mlg"
```

### Exemplo 2: Loop com Contador
```tomlang
int i = 0;
printf("Contando: ");
while (i < 5) {
  printf(i);
  i = i + 1;
}
```

### Exemplo 3: Fatorial (Recursão via Loop)
```tomlang
printf("Calcule o fatorial de: ");
int n;
scanf(n);

int resultado = 1;
int i = 1;
while (i <= n) {
  resultado = resultado * i;
  i = i + 1;
}

printf("O fatorial é: ");
printf(resultado);
```

### Exemplo 4: Switch-Case
```tomlang
printf("Digite um dia (1-7): ");
int dia;
scanf(dia);

switch (dia) {
  case 1: printf("Segunda"); break;
  case 2: printf("Terça");   break;
  case 3: printf("Quarta");  break;
  case 4: printf("Quinta");  break;
  case 5: printf("Sexta");   break;
  case 6: printf("Sábado");  break;
  case 7: printf("Domingo"); break;
  default: printf("Dia inválido");
}
```

### Exemplo 5: Do-While
```tomlang
int i = 0;
do {
  printf(i);
  i = i + 1;
} while (i < 3);
```

### Exemplo 6: For Loop
```tomlang
for (int i = 0; i < 10; i = i + 1) {
  printf(i);
}
```

---

## Testando o Compilador

### Teste de Sucesso
```powershell
.\gradlew.bat run --args "tests\ok.mlg"
```

Saída esperada:
```
inicio
0
1
2
umum
dois
Ola, ... (aguarda entrada)
```

### Detectar Erros Sintáticos
Crie `tests/sintatico.mlg` com erro:
```tomlang
int x = 5
printf(x);
```
(Falta `;` na primeira linha)

```powershell
.\gradlew.bat run --args "tests\sintatico.mlg"
```

Saída esperada:
```
Falha de compilação (erros sintáticos):
line 1:12: erro...
```

### Detectar Erros Semânticos
Crie `tests/semantico.mlg`:
```tomlang
int x = 5;
printf(y);  // Variável 'y' não declarada
```

```powershell
.\gradlew.bat run --args "tests\semantico.mlg"
```

Saída esperada:
```
Falha de compilação (erros semânticos):
line 2: Variável 'y' não declarada
```

---

## Componentes Principais

### `TomLangMain.java`
Orquestra todo o pipeline de compilação:
1. Lê arquivo de entrada
2. Lexer tokeniza o código
3. Parser cria a AST
4. SemanticVisitor valida tipos e declarações
5. InterpreterVisitor executa o programa

### `SemanticVisitor.java`
Realiza análise semântica:
- Verifica declaração de todas as variáveis antes do uso
- Valida atribuições (compatibilidade de tipos)
- Valida tipos em expressões
- Valida tipos em condições (não pode ser string)
- Suporta scopes em blocos e loops

### `InterpreterVisitor.java`
Executa o programa percorrendo a AST:
- Avalia expressões com precedência correta
- Executa statements em ordem
- Gerencia variáveis e seus valores
- Implementa `scanf` e `printf`

### `SymbolTable.java`
Gerencia variáveis, tipos e scopes:
- Tabela de símbolo com suporte a scopes
- Verificação de redeclaração
- Lookup de variáveis em scopes encadeados

### `TomLangErrorListener.java`
Coleta erros sintáticos do parser ANTLR:
- Formata mensagens de erro com linha e coluna
- Previne execução se houver erros

---

## Fases de Compilação Detalhadas

### 1. Análise Léxica (Lexer)
O `TomLangLexer` converte o texto em tokens:
```
Entrada:  int x = 5 + 3;
Tokens:   [int] [ID:x] [=] [INT:5] [+] [INT:3] [;]
```

### 2. Análise Sintática (Parser)
O `TomLangParser` constrói a AST usando a gramática:
```
         prog
          |
        decl
       / | \ \
     int ID = expr ;
        x       |
            addExpr
            / | \
        INT + INT
         5      3
```

### 3. Análise Semântica
O `SemanticVisitor` valida a AST:
- Verifica se `x` foi declarado ✓
- Verifica se tipo `int` é compatível com `5 + 3` ✓

### 4. Interpretação
O `InterpreterVisitor` executa a AST:
- Declara `x` na symbol table
- Calcula `5 + 3 = 8`
- Atribui `8` para `x`

---

## Configuração Avançada (Gradle)

### Alterar Versão Java
Edite `build.gradle`:
```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)  // Mude aqui
    }
}
```

### Alterar Versão ANTLR
Edite `build.gradle`:
```groovy
dependencies {
    antlr "org.antlr:antlr4:4.13.2"  // Mude aqui
    implementation "org.antlr:antlr4-runtime:4.13.2"  // E aqui
}
```

### Comandos Gradle Úteis
```powershell
# Verificar tarefas disponíveis
.\gradlew.bat tasks

# Limpar build anterior
.\gradlew.bat clean

# Compilar sem executar
.\gradlew.bat classes

# Executar testes (se configurados)
.\gradlew.bat test

# Ver dependências
.\gradlew.bat dependencies
```

---

## Troubleshooting

| Problema | Solução |
|----------|---------|
| `gradle: command not found` | Use `.\gradlew.bat` (Windows) ou `./gradlew` (Linux/macOS) |
| `Java version not supported` | Verifique: `java -version`. Instale Java 21+ |
| `File not found: tests/ok.mlg` | Certifique-se que está no diretório raiz do projeto |
| `Variável não declarada` | Declare a variável antes de usá-la (ex: `int x = 0;`) |
| `Erro de compilação ao fazer build` | Limpe com `.\gradlew.bat clean` e reconstrua |
| `erro de tipo incompatível` | Verifique tipos em atribuições e operações |
| `break fora de loop/switch` | `break` só é permitido dentro de loops ou switches |

---

## Referência da Gramática

Veja o arquivo completo em `grammar/TomLang.g4`. Resumo:

- **Programa**: Sequência de declarações e statements
- **Declaração**: `tipo id [= expr];`
- **Tipos**: `int`, `float`, `string`
- **Statements**: Atribuição, if, while, do-while, for, switch, break, printf, scanf
- **Expressões**: Operadores com precedência padrão (lógico < relacional < aritmético)

---

## Quick Start (Resumo Rápido)

1. **Clone/Abra o projeto**
   ```powershell
   cd c:\Users\Adailton\Documents\projetos\compilador\compilador
   ```

2. **Compile**
   ```powershell
   .\gradlew.bat build
   ```

3. **Execute um exemplo**
   ```powershell
   .\gradlew.bat run --args "tests\ok.mlg"
   ```

4. **Crie seu próprio programa** em `tests/seu_programa.mlg` e execute:
   ```powershell
   .\gradlew.bat run --args "tests\seu_programa.mlg"
   ```

---

## Referências

- **ANTLR 4**: https://www.antlr.org/
- **Java Documentation**: https://docs.oracle.com/en/java/
- **Gradle**: https://gradle.org/

