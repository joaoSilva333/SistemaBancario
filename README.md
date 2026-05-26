# 🏦 Sistema Bancário Simples

Projeto desenvolvido para praticar **Java com OOP** e **SQL Server com JDBC**, simulando operações básicas de um banco real.

## ✨ Funcionalidades

- ✅ Cadastro de clientes (com validação de CPF duplicado)
- ✅ Abertura de contas (Corrente ou Poupança)
- ✅ Depósito
- ✅ Saque (com validação de saldo)
- ✅ Transferência entre contas (com rollback em caso de erro)
- ✅ Extrato completo de movimentações

## 🗂️ Estrutura do Projeto

```
src/
└── main/java/br/com/bancosimples/
    ├── Main.java                         ← ponto de entrada
    ├── model/
    │   ├── Cliente.java                  ← entidade cliente
    │   ├── Conta.java                    ← entidade conta
    │   └── Transacao.java                ← entidade transação
    ├── repository/
    │   ├── ClienteRepository.java        ← SQL de clientes
    │   ├── ContaRepository.java          ← SQL de contas
    │   └── TransacaoRepository.java      ← SQL de transações
    ├── service/
    │   └── BancoService.java             ← regras de negócio
    ├── ui/
    │   └── Menu.java                     ← interface console
    └── util/
        └── ConnectionFactory.java        ← conexão JDBC
```

## 🛠️ Tecnologias

- **Java 17+**
- **SQL Server** (banco de dados)
- **JDBC** (conexão Java ↔ SQL Server)

## ⚙️ Como rodar

### 1. Banco de dados
Execute o script `banco_sistema.sql` no **SQL Server Management Studio (SSMS)**.

### 2. Driver JDBC
Baixe o driver em: https://learn.microsoft.com/pt-br/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server  
Adicione o `.jar` ao classpath do projeto.

### 3. Configurar conexão
Edite o arquivo `ConnectionFactory.java` com os seus dados:

```java
private static final String HOST    = "localhost";
private static final String BANCO   = "SistemaBancario";
private static final String USUARIO = "sa";
private static final String SENHA   = "sua_senha";
```

### 4. Executar
Rode a classe `Main.java` e o menu aparece no console.

## 📐 Conceitos aplicados

| Conceito | Onde aparece |
|---|---|
| Orientação a Objetos (OOP) | Classes model com encapsulamento |
| Camadas (MVC simplificado) | model / repository / service / ui |
| JDBC | ConnectionFactory + Repositories |
| Transação SQL (commit/rollback) | BancoService — transferência e saque |
| PreparedStatement | Todos os SQLs (prevenção de SQL Injection) |
| Tratamento de exceções | Service valida regras de negócio |

## 👤 Autor

**João Vitor Alves da Silva**  
Estudante de Ciência da Computação — USF Itatiba  
[joao.silva33sp@gmail.com](mailto:joao.silva33sp@gmail.com)
