-- ============================================================
--  SISTEMA BANCÁRIO SIMPLES
--  Autor: João Vitor Alves da Silva
--  Banco: SQL Server
-- ============================================================

-- Cria e usa o banco de dados
CREATE DATABASE SistemaBancario;
GO
USE SistemaBancario;
GO

-- ============================================================
--  TABELA: Cliente
--  Representa uma pessoa que tem conta no banco
-- ============================================================
CREATE TABLE Cliente (
    id_cliente   INT           PRIMARY KEY IDENTITY(1,1), -- gerado automaticamente
    nome         VARCHAR(100)  NOT NULL,
    cpf          CHAR(11)      NOT NULL UNIQUE,           -- 11 dígitos, único por pessoa
    email        VARCHAR(100),
    telefone     VARCHAR(15),
    data_cadastro DATETIME     DEFAULT GETDATE()          -- data/hora do cadastro
);
GO

-- ============================================================
--  TABELA: TipoConta
--  Define os tipos possíveis de conta (corrente, poupança)
-- ============================================================
CREATE TABLE TipoConta (
    id_tipo  INT          PRIMARY KEY IDENTITY(1,1),
    descricao VARCHAR(50) NOT NULL                        -- ex: 'Corrente', 'Poupança'
);
GO

-- Inserindo os tipos de conta já fixos
INSERT INTO TipoConta (descricao) VALUES ('Corrente');
INSERT INTO TipoConta (descricao) VALUES ('Poupança');
GO

-- ============================================================
--  TABELA: Conta
--  Cada cliente pode ter uma ou mais contas
-- ============================================================
CREATE TABLE Conta (
    id_conta      INT           PRIMARY KEY IDENTITY(1,1),
    numero_conta  VARCHAR(10)   NOT NULL UNIQUE,          -- ex: '0001-2'
    id_cliente    INT           NOT NULL,
    id_tipo       INT           NOT NULL,
    saldo         DECIMAL(15,2) NOT NULL DEFAULT 0.00,    -- saldo atual
    ativa         BIT           NOT NULL DEFAULT 1,       -- 1 = ativa, 0 = encerrada
    data_abertura DATETIME      DEFAULT GETDATE(),

    -- Chaves estrangeiras
    CONSTRAINT FK_Conta_Cliente  FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente),
    CONSTRAINT FK_Conta_Tipo     FOREIGN KEY (id_tipo)    REFERENCES TipoConta(id_tipo),

    -- Saldo nunca pode ser negativo
    CONSTRAINT CK_Saldo_Positivo CHECK (saldo >= 0)
);
GO

-- ============================================================
--  TABELA: TipoTransacao
--  Define os tipos de movimentação possíveis
-- ============================================================
CREATE TABLE TipoTransacao (
    id_tipo   INT          PRIMARY KEY IDENTITY(1,1),
    descricao VARCHAR(50)  NOT NULL                       -- ex: 'Depósito', 'Saque'
);
GO

INSERT INTO TipoTransacao (descricao) VALUES ('Depósito');
INSERT INTO TipoTransacao (descricao) VALUES ('Saque');
INSERT INTO TipoTransacao (descricao) VALUES ('Transferência Enviada');
INSERT INTO TipoTransacao (descricao) VALUES ('Transferência Recebida');
GO

-- ============================================================
--  TABELA: Transacao
--  Registra cada movimentação feita nas contas
-- ============================================================
CREATE TABLE Transacao (
    id_transacao    INT           PRIMARY KEY IDENTITY(1,1),
    id_conta        INT           NOT NULL,               -- conta que fez a operação
    id_tipo         INT           NOT NULL,               -- tipo da transação
    valor           DECIMAL(15,2) NOT NULL,               -- valor movimentado
    saldo_apos      DECIMAL(15,2) NOT NULL,               -- saldo depois da operação
    id_conta_destino INT          NULL,                   -- preenchido só em transferências
    data_transacao  DATETIME      DEFAULT GETDATE(),
    descricao       VARCHAR(200)  NULL,                   -- observação livre

    CONSTRAINT FK_Transacao_Conta    FOREIGN KEY (id_conta)         REFERENCES Conta(id_conta),
    CONSTRAINT FK_Transacao_Tipo     FOREIGN KEY (id_tipo)          REFERENCES TipoTransacao(id_tipo),
    CONSTRAINT FK_Transacao_Destino  FOREIGN KEY (id_conta_destino) REFERENCES Conta(id_conta),

    -- Valor sempre positivo
    CONSTRAINT CK_Valor_Positivo CHECK (valor > 0)
);
GO

-- ============================================================
--  VIEWS ÚTEIS
--  Consultas prontas que você pode chamar direto do Java
-- ============================================================

-- View: dados completos de cada conta com nome do cliente
CREATE VIEW vw_Contas AS
    SELECT
        c.id_conta,
        c.numero_conta,
        cl.nome        AS nome_cliente,
        cl.cpf,
        tc.descricao   AS tipo_conta,
        c.saldo,
        c.ativa,
        c.data_abertura
    FROM Conta c
    JOIN Cliente    cl ON cl.id_cliente = c.id_cliente
    JOIN TipoConta  tc ON tc.id_tipo    = c.id_tipo;
GO

-- View: extrato completo com nome do tipo de transação
CREATE VIEW vw_Extrato AS
    SELECT
        t.id_transacao,
        c.numero_conta,
        tt.descricao      AS tipo_transacao,
        t.valor,
        t.saldo_apos,
        t.data_transacao,
        t.descricao       AS observacao,
        cd.numero_conta   AS conta_destino
    FROM Transacao t
    JOIN Conta          c  ON c.id_conta   = t.id_conta
    JOIN TipoTransacao  tt ON tt.id_tipo   = t.id_tipo
    LEFT JOIN Conta     cd ON cd.id_conta  = t.id_conta_destino;
GO

-- ============================================================
--  DADOS DE TESTE
--  Apague depois — só pra você ver funcionando
-- ============================================================

INSERT INTO Cliente (nome, cpf, email, telefone)
VALUES ('João Vitor Alves da Silva', '12345678901', 'joao.silva33sp@gmail.com', '11940227818');

INSERT INTO Conta (numero_conta, id_cliente, id_tipo, saldo)
VALUES ('0001-0', 1, 1, 1000.00);  -- conta corrente com R$1000

-- Simula um depósito de R$500
INSERT INTO Transacao (id_conta, id_tipo, valor, saldo_apos, descricao)
VALUES (1, 1, 500.00, 1500.00, 'Depósito inicial');

-- Conferindo tudo
SELECT * FROM vw_Contas;
SELECT * FROM vw_Extrato;
GO

-- ============================================================
--  REFERÊNCIA RÁPIDA PARA O JAVA
--
--  Consultar saldo:
--    SELECT saldo FROM Conta WHERE numero_conta = ?
--
--  Depositar (fazer em transação SQL):
--    UPDATE Conta SET saldo = saldo + ? WHERE id_conta = ?
--    INSERT INTO Transacao (id_conta, id_tipo, valor, saldo_apos) VALUES (?, 1, ?, ?)
--
--  Sacar:
--    UPDATE Conta SET saldo = saldo - ? WHERE id_conta = ? AND saldo >= ?
--    INSERT INTO Transacao (id_conta, id_tipo, valor, saldo_apos) VALUES (?, 2, ?, ?)
--
--  Transferir (duas atualizações + dois registros de transação):
--    BEGIN TRANSACTION
--      UPDATE Conta SET saldo = saldo - ? WHERE id_conta = ?   -- origem
--      UPDATE Conta SET saldo = saldo + ? WHERE id_conta = ?   -- destino
--      INSERT INTO Transacao ... (tipo 3 - Enviada)
--      INSERT INTO Transacao ... (tipo 4 - Recebida)
--    COMMIT
-- ============================================================
