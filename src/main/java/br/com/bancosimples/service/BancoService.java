package br.com.bancosimples.service;

import br.com.bancosimples.model.Conta;
import br.com.bancosimples.model.Cliente;
import br.com.bancosimples.model.Transacao;
import br.com.bancosimples.repository.ClienteRepository;
import br.com.bancosimples.repository.ContaRepository;
import br.com.bancosimples.repository.TransacaoRepository;
import br.com.bancosimples.util.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Camada de serviço — contém toda a lógica de negócio do banco.
 * Aqui ficam as regras: saldo insuficiente, CPF duplicado, etc.
 */
public class BancoService {

    private final ClienteRepository    clienteRepo    = new ClienteRepository();
    private final ContaRepository      contaRepo      = new ContaRepository();
    private final TransacaoRepository  transacaoRepo  = new TransacaoRepository();

    // ─────────────────────────────────────────────────
    //  CLIENTES
    // ─────────────────────────────────────────────────

    public Cliente cadastrarCliente(String nome, String cpf, String email, String telefone)
            throws SQLException {

        // Regra: CPF não pode ser duplicado
        if (clienteRepo.buscarPorCpf(cpf) != null) {
            throw new IllegalArgumentException("Já existe um cliente com o CPF informado.");
        }

        Cliente c = new Cliente(nome, cpf, email, telefone);
        return clienteRepo.cadastrar(c);
    }

    public Cliente buscarClientePorCpf(String cpf) throws SQLException {
        return clienteRepo.buscarPorCpf(cpf);
    }

    public List<Cliente> listarClientes() throws SQLException {
        return clienteRepo.listarTodos();
    }

    // ─────────────────────────────────────────────────
    //  CONTAS
    // ─────────────────────────────────────────────────

    /**
     * @param idTipo 1 = Corrente, 2 = Poupança
     */
    public Conta abrirConta(int idCliente, int idTipo) throws SQLException {

        // Regra: cliente deve existir
        if (clienteRepo.buscarPorId(idCliente) == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        String numero = contaRepo.gerarNumeroConta();
        Conta c = new Conta(numero, idCliente, idTipo);
        return contaRepo.abrirConta(c);
    }

    public Conta buscarConta(String numeroConta) throws SQLException {
        return contaRepo.buscarPorNumero(numeroConta);
    }

    public List<Conta> listarContasDoCliente(int idCliente) throws SQLException {
        return contaRepo.listarPorCliente(idCliente);
    }

    // ─────────────────────────────────────────────────
    //  OPERAÇÕES FINANCEIRAS
    // ─────────────────────────────────────────────────

    /** Deposita um valor na conta. */
    public void depositar(String numeroConta, BigDecimal valor) throws SQLException {

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser positivo.");
        }

        Conta conta = contaRepo.buscarPorNumero(numeroConta);
        if (conta == null) throw new IllegalArgumentException("Conta não encontrada.");
        if (!conta.isAtiva()) throw new IllegalArgumentException("Conta encerrada.");

        BigDecimal novoSaldo = conta.getSaldo().add(valor);

        Connection conn = ConnectionFactory.getConnection();
        try {
            conn.setAutoCommit(false); // início da transação SQL

            contaRepo.atualizarSaldo(conta.getIdConta(), novoSaldo, conn);

            Transacao t = new Transacao(conta.getIdConta(), 1, valor, novoSaldo, "Depósito");
            transacaoRepo.registrar(t, conn);

            conn.commit(); // confirma tudo

        } catch (Exception e) {
            conn.rollback(); // desfaz se algo deu errado
            throw e;
        } finally {
            ConnectionFactory.fecharConexao(conn);
        }
    }

    /** Saca um valor da conta. */
    public void sacar(String numeroConta, BigDecimal valor) throws SQLException {

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser positivo.");
        }

        Conta conta = contaRepo.buscarPorNumero(numeroConta);
        if (conta == null) throw new IllegalArgumentException("Conta não encontrada.");
        if (!conta.isAtiva()) throw new IllegalArgumentException("Conta encerrada.");

        // Regra principal: saldo suficiente
        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException(
                "Saldo insuficiente. Saldo atual: R$ " + conta.getSaldo());
        }

        BigDecimal novoSaldo = conta.getSaldo().subtract(valor);

        Connection conn = ConnectionFactory.getConnection();
        try {
            conn.setAutoCommit(false);

            contaRepo.atualizarSaldo(conta.getIdConta(), novoSaldo, conn);

            Transacao t = new Transacao(conta.getIdConta(), 2, valor, novoSaldo, "Saque");
            transacaoRepo.registrar(t, conn);

            conn.commit();

        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            ConnectionFactory.fecharConexao(conn);
        }
    }

    /** Transfere valor entre duas contas. */
    public void transferir(String numeroOrigem, String numeroDestino, BigDecimal valor)
            throws SQLException {

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser positivo.");
        }

        if (numeroOrigem.equals(numeroDestino)) {
            throw new IllegalArgumentException("Origem e destino não podem ser a mesma conta.");
        }

        Conta origem  = contaRepo.buscarPorNumero(numeroOrigem);
        Conta destino = contaRepo.buscarPorNumero(numeroDestino);

        if (origem  == null) throw new IllegalArgumentException("Conta de origem não encontrada.");
        if (destino == null) throw new IllegalArgumentException("Conta de destino não encontrada.");
        if (!origem.isAtiva())  throw new IllegalArgumentException("Conta de origem encerrada.");
        if (!destino.isAtiva()) throw new IllegalArgumentException("Conta de destino encerrada.");

        if (origem.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException(
                "Saldo insuficiente. Saldo atual: R$ " + origem.getSaldo());
        }

        BigDecimal saldoOrigem  = origem.getSaldo().subtract(valor);
        BigDecimal saldoDestino = destino.getSaldo().add(valor);

        Connection conn = ConnectionFactory.getConnection();
        try {
            conn.setAutoCommit(false);

            // Atualiza os dois saldos
            contaRepo.atualizarSaldo(origem.getIdConta(),  saldoOrigem,  conn);
            contaRepo.atualizarSaldo(destino.getIdConta(), saldoDestino, conn);

            // Registra a saída na origem (tipo 3 = Transferência Enviada)
            Transacao tOrigem = new Transacao(origem.getIdConta(), 3, valor, saldoOrigem,
                "Transferência para conta " + numeroDestino);
            tOrigem.setIdContaDestino(destino.getIdConta());
            transacaoRepo.registrar(tOrigem, conn);

            // Registra a entrada no destino (tipo 4 = Transferência Recebida)
            Transacao tDestino = new Transacao(destino.getIdConta(), 4, valor, saldoDestino,
                "Transferência da conta " + numeroOrigem);
            transacaoRepo.registrar(tDestino, conn);

            conn.commit();

        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            ConnectionFactory.fecharConexao(conn);
        }
    }

    /** Retorna o extrato de uma conta pelo número. */
    public List<Transacao> verExtrato(String numeroConta) throws SQLException {
        Conta conta = contaRepo.buscarPorNumero(numeroConta);
        if (conta == null) throw new IllegalArgumentException("Conta não encontrada.");
        return transacaoRepo.buscarExtrato(conta.getIdConta());
    }
}
