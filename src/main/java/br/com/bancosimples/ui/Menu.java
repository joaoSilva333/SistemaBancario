package br.com.bancosimples.ui;

import br.com.bancosimples.model.Conta;
import br.com.bancosimples.model.Cliente;
import br.com.bancosimples.model.Transacao;
import br.com.bancosimples.service.BancoService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Interface do usuário via console.
 * Toda entrada/saída de dados fica aqui — o service não sabe que existe um menu.
 */
public class Menu {

    private final BancoService service = new BancoService();
    private final Scanner      scanner = new Scanner(System.in);

    public void iniciar() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       SISTEMA BANCÁRIO SIMPLES       ║");
        System.out.println("╚══════════════════════════════════════╝");

        boolean rodando = true;
        while (rodando) {
            exibirMenuPrincipal();
            int opcao = lerInt("Escolha uma opção: ");

            try {
                switch (opcao) {
                    case 1  -> menuClientes();
                    case 2  -> menuContas();
                    case 3  -> menuOperacoes();
                    case 0  -> { System.out.println("\nAté logo!"); rodando = false; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("\n❌ Erro: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("\n❌ Erro de banco de dados: " + e.getMessage());
            }
        }
    }

    // ─────────────────────────────────────────────────
    //  MENUS
    // ─────────────────────────────────────────────────

    private void exibirMenuPrincipal() {
        System.out.println("\n──────────────────────────────────────");
        System.out.println("  1. Clientes");
        System.out.println("  2. Contas");
        System.out.println("  3. Operações (depósito, saque, transferência)");
        System.out.println("  0. Sair");
        System.out.println("──────────────────────────────────────");
    }

    private void menuClientes() throws SQLException {
        System.out.println("\n── CLIENTES ──");
        System.out.println("  1. Cadastrar cliente");
        System.out.println("  2. Buscar por CPF");
        System.out.println("  3. Listar todos");

        int opcao = lerInt("Opção: ");
        switch (opcao) {
            case 1 -> cadastrarCliente();
            case 2 -> buscarCliente();
            case 3 -> listarClientes();
        }
    }

    private void menuContas() throws SQLException {
        System.out.println("\n── CONTAS ──");
        System.out.println("  1. Abrir conta");
        System.out.println("  2. Consultar conta");

        int opcao = lerInt("Opção: ");
        switch (opcao) {
            case 1 -> abrirConta();
            case 2 -> consultarConta();
        }
    }

    private void menuOperacoes() throws SQLException {
        System.out.println("\n── OPERAÇÕES ──");
        System.out.println("  1. Depósito");
        System.out.println("  2. Saque");
        System.out.println("  3. Transferência");
        System.out.println("  4. Extrato");

        int opcao = lerInt("Opção: ");
        switch (opcao) {
            case 1 -> depositar();
            case 2 -> sacar();
            case 3 -> transferir();
            case 4 -> verExtrato();
        }
    }

    // ─────────────────────────────────────────────────
    //  AÇÕES DE CLIENTE
    // ─────────────────────────────────────────────────

    private void cadastrarCliente() throws SQLException {
        System.out.println("\n── Cadastrar Cliente ──");
        String nome     = lerTexto("Nome completo: ");
        String cpf      = lerTexto("CPF (só números): ");
        String email    = lerTexto("E-mail: ");
        String telefone = lerTexto("Telefone: ");

        Cliente c = service.cadastrarCliente(nome, cpf, email, telefone);
        System.out.println("✅ Cliente cadastrado! ID: " + c.getIdCliente());
    }

    private void buscarCliente() throws SQLException {
        String cpf = lerTexto("CPF: ");
        Cliente c  = service.buscarClientePorCpf(cpf);
        if (c == null) {
            System.out.println("Cliente não encontrado.");
        } else {
            System.out.println("\n── Dados do cliente ──");
            System.out.println("Nome:     " + c.getNome());
            System.out.println("CPF:      " + c.getCpf());
            System.out.println("E-mail:   " + c.getEmail());
            System.out.println("Telefone: " + c.getTelefone());
        }
    }

    private void listarClientes() throws SQLException {
        List<Cliente> lista = service.listarClientes();
        if (lista.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        System.out.println("\n── Clientes ──");
        for (Cliente c : lista) {
            System.out.printf("  [%d] %s — CPF: %s%n",
                c.getIdCliente(), c.getNome(), c.getCpf());
        }
    }

    // ─────────────────────────────────────────────────
    //  AÇÕES DE CONTA
    // ─────────────────────────────────────────────────

    private void abrirConta() throws SQLException {
        System.out.println("\n── Abrir Conta ──");
        int idCliente = lerInt("ID do cliente: ");
        System.out.println("Tipo: 1 = Corrente  |  2 = Poupança");
        int tipo = lerInt("Tipo da conta: ");

        Conta c = service.abrirConta(idCliente, tipo);
        System.out.println("✅ Conta aberta! Número: " + c.getNumeroConta());
    }

    private void consultarConta() throws SQLException {
        String numero = lerTexto("Número da conta: ");
        Conta c = service.buscarConta(numero);
        if (c == null) {
            System.out.println("Conta não encontrada.");
        } else {
            System.out.println("\n── Dados da conta ──");
            System.out.println("Número:  " + c.getNumeroConta());
            System.out.println("Titular: " + c.getNomeCliente());
            System.out.println("Tipo:    " + c.getTipoConta());
            System.out.printf( "Saldo:   R$ %.2f%n", c.getSaldo());
            System.out.println("Ativa:   " + (c.isAtiva() ? "Sim" : "Não"));
        }
    }

    // ─────────────────────────────────────────────────
    //  OPERAÇÕES FINANCEIRAS
    // ─────────────────────────────────────────────────

    private void depositar() throws SQLException {
        System.out.println("\n── Depósito ──");
        String numero = lerTexto("Número da conta: ");
        BigDecimal valor = lerDecimal("Valor: R$ ");

        service.depositar(numero, valor);
        System.out.println("✅ Depósito realizado com sucesso!");
    }

    private void sacar() throws SQLException {
        System.out.println("\n── Saque ──");
        String numero = lerTexto("Número da conta: ");
        BigDecimal valor = lerDecimal("Valor: R$ ");

        service.sacar(numero, valor);
        System.out.println("✅ Saque realizado com sucesso!");
    }

    private void transferir() throws SQLException {
        System.out.println("\n── Transferência ──");
        String origem  = lerTexto("Conta de origem: ");
        String destino = lerTexto("Conta de destino: ");
        BigDecimal valor = lerDecimal("Valor: R$ ");

        service.transferir(origem, destino, valor);
        System.out.println("✅ Transferência realizada com sucesso!");
    }

    private void verExtrato() throws SQLException {
        String numero = lerTexto("Número da conta: ");
        List<Transacao> extrato = service.verExtrato(numero);

        if (extrato.isEmpty()) {
            System.out.println("Nenhuma transação encontrada.");
            return;
        }

        System.out.println("\n── Extrato da conta " + numero + " ──");
        System.out.printf("%-25s %-22s %10s %14s%n",
            "Data", "Tipo", "Valor", "Saldo após");
        System.out.println("─".repeat(75));

        for (Transacao t : extrato) {
            System.out.printf("%-25s %-22s R$%8.2f  R$%10.2f%n",
                t.getDataTransacao(),
                t.getTipoTransacao(),
                t.getValor(),
                t.getSaldoApos());
        }
    }

    // ─────────────────────────────────────────────────
    //  UTILITÁRIOS DE LEITURA
    // ─────────────────────────────────────────────────

    private String lerTexto(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int lerInt(String prompt) {
        System.out.print(prompt);
        try {
            int val = Integer.parseInt(scanner.nextLine().trim());
            return val;
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido. Usando 0.");
            return 0;
        }
    }

    private BigDecimal lerDecimal(String prompt) {
        System.out.print(prompt);
        try {
            return new BigDecimal(scanner.nextLine().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inválido.");
        }
    }
}
