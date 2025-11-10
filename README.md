# TomLang Compilador

Um compilador e intérprete para a linguagem **TomLang**, uma linguagem de programação imperativa simples desenvolvida com **ANTLR 4** e **Java**.

## 📋 Sobre o Projeto

TomLang é uma linguagem de programação de propósito educacional que implementa:
- **Análise Léxica e Sintática** via ANTLR 4
- **Verificação Semântica** com tabela de símbolos
- **Interpretação/Execução** do código compilado

O projeto é estruturado em três fases do compilador:
1. **Lexer**: Tokeniza o código-fonte
2. **Parser**: Cria a árvore de sintaxe abstrata (AST)
3. **Interpreter**: Executa o programa

## 🔧 Características da Linguagem

### Tipos de Dados
- `int` - Números inteiros
- `float` - Números em ponto flutuante
- `string` - Cadeias de texto

### Estruturas de Controle
- **Condicional**: `if-else`
- **Repetição**: `while`, `do-while`, `for`
- **Seleção**: `switch-case` com `default`
- **Salto**: `break`

### Operadores
- **Aritmético**: `+`, `-`, `*`, `/`
- **Relacional**: `<`, `>`, `<=`, `>=`
- **Comparação**: `==`, `!=`
- **Lógico**: `&&`, `||`, `!`

### Operações Built-in
- `printf(valor)` - Imprime na tela
- `scanf(variável)` - Lê entrada do usuário

## 📁 Estrutura do Projeto

```
compilador/
├── README.md                    # Este arquivo
├── compilador.iml              # Configuração do IntelliJ IDEA
├── grammar/
│   └── TomLang.g4              # Gramática ANTLR 4
├── src/
│   ├── TomLangMain.java        # Ponto de entrada do compilador
│   ├── InterpreterVisitor.java # Visitor para interpretação
│   ├── SemanticVisitor.java    # Visitor para análise semântica
│   ├── SymbolTable.java        # Tabela de símbolos
│   ├── TomLangErrorListener.java # Tratamento de erros
│   └── [Arquivos gerados pelo ANTLR]
└── tests/
    ├── ok.mlg                  # Código de exemplo válido
    └── error.mlg               # Código com erros
```

## 🚀 Como Usar

### Pré-requisitos
- **Java 11** ou superior
- **ANTLR 4** (antlr-4.x-complete.jar)
- Maven ou ferramenta de build similar (opcional)

### Compilação

1. **Gerar código do ANTLR** (se necessário):
```bash
antlr4 -visitor grammar/TomLang.g4 -o src/
```

2. **Compilar os arquivos Java**:
```bash
javac -cp ".;antlr-4.13.0-complete.jar" src/*.java
```

### Execução

Execute um programa TomLang:
```bash
java -cp ".;antlr-4.13.0-complete.jar" TomLangMain arquivo.tl
```

### Exemplo de Código TomLang

```tomlang
int i = 0;
printf("inicio");
do {
  printf(i);
  i = i + 1;
} while (i < 3);

int x = 2;
switch (x) {
  case 1: printf("um");
    break;
  case 2: printf("dois");
    break;
  default: printf("outro");
}

string nome;
printf("digite seu nome:");
scanf(nome);
printf("Ola, " + nome);
```

## 📝 Sintaxe da Linguagem

### Declaração de Variáveis
```tomlang
int idade = 25;
float altura = 1.75;
string nome = "João";
```

### Estruturas de Controle

**If-Else:**
```tomlang
if (idade >= 18) {
  printf("Maior de idade");
} else {
  printf("Menor de idade");
}
```

**While:**
```tomlang
int i = 0;
while (i < 10) {
  printf(i);
  i = i + 1;
}
```

**Do-While:**
```tomlang
int i = 0;
do {
  printf(i);
  i = i + 1;
} while (i < 3);
```

**For:**
```tomlang
for (int i = 0; i < 5; i = i + 1) {
  printf(i);
}
```

**Switch-Case:**
```tomlang
int opcao = 2;
switch (opcao) {
  case 1: printf("Um");
    break;
  case 2: printf("Dois");
    break;
  default: printf("Outro");
}
```

### Entrada/Saída
```tomlang
printf("Digite seu nome: ");
scanf(nome);
printf("Olá, " + nome);
```

## 🧪 Testes

### Exemplo de Código Válido (ok.mlg)
```tomlang
int i = 0;
printf("inicio");
do {
  printf(i);
  i = i + 1;
} while (i < 3);
```

### Executar Teste
```bash
java -cp ".;antlr-4.13.0-complete.jar" TomLangMain tests/ok.mlg
```

## 🔍 Componentes Principais

### TomLangMain.java
Ponto de entrada do compilador. Responsável por:
- Ler o arquivo de entrada
- Executar o lexer e parser
- Chamar o verificador semântico
- Executar o interpretador

### SemanticVisitor.java
Realiza análise semântica:
- Verifica declarações de variáveis
- Valida tipos em expressões
- Detecta usos de variáveis não declaradas
- Detecta redeclarações de variáveis

### InterpreterVisitor.java
Executa o programa visitando a AST:
- Avalia expressões
- Executa statements
- Gerencia valores de variáveis em tempo de execução

### SymbolTable.java
Tabela de símbolos para armazenar informações sobre:
- Variáveis declaradas
- Tipos de dados
- Escopos (se aplicável)

### TomLangErrorListener.java
Tratamento de erros sintáticos:
- Coleta erros durante parsing
- Fornece mensagens de erro detalhadas

## 📚 Gramática ANTLR 4

A gramática está definida em `grammar/TomLang.g4` e segue as seguintes regras principais:

```antlr
prog        : (decl | stmt)* EOF ;
decl        : type ID ('=' expr)? ';' ;
type        : 'int' | 'float' | 'string' ;
stmt        : assign ';' | ifStmt | whileStmt | ... ;
expr        : orExpr ;
```

Para detalhes completos da gramática, consulte o arquivo `grammar/TomLang.g4`.

## 🐛 Tratamento de Erros

O compilador detecta dois tipos de erros:

1. **Erros Sintáticos**: Detectados pelo parser (violações da gramática)
   ```
   Falha de compilação (erros sintáticos):
   ```

2. **Erros Semânticos**: Detectados pelo verificador semântico
   ```
   Falha de compilação (erros semânticos):
   ```

Ambos os erros causam saída com código de erro 1.

## 📖 Exemplos Adicionais

### Calcula Fatorial
```tomlang
int n = 5;
int resultado = 1;
for (int i = 1; i <= n; i = i + 1) {
  resultado = resultado * i;
}
printf(resultado);
```

### Entrada do Usuário
```tomlang
printf("Digite um número: ");
int numero;
scanf(numero);
printf("O número é: " + numero);
```

## 🔗 Dependências

- **ANTLR 4**: Framework para geração de lexers e parsers
- **Java Runtime Environment (JRE)**: Para executar o compilador