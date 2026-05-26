package br.com.bancosimples.repository;

import br.com.bancosimples.model.Conta;
import br.com.bancosimples.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por todas as operações de banco de dados da entidade Conta.
 */
public class ContaRepository {

    // ── Abrir nova conta ──────────────────────────────
    public Conta abrirConta(Conta conta) throws SQLException {
        String sql = "INSERT INTO Conta (numero_conta, id_cliente, id_tipo, saldo) "
                   + "VALUES (?, ?, ?, ?)";

        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, conta.getNumeroConta());
            ps.setInt(2, conta.getIdCliente());
            ps.setInt(3, conta.getIdTipo());
            ps.setBigDecimal(4, conta.getSaldo());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                conta.setIdConta(rs.getInt(1));
            }
            return conta;

        } finally {
            ConnectionFactory.fecharConexao(conn);
        }
    }

    // ── Buscar por número da conta ────────────────────
    public Conta buscarPorNumero(String numeroConta) throws SQLException {
        String sql = "SELECT * FROM vw_Contas WHERE numero_conta = ?";

        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numeroConta);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearConta(rs);
            }
            return null;

        } finally {
            ConnectionFactory.fecharConexao(conn);
        }
    }

    // ── Listar contas de um cliente ───────────────────
    public List<Conta> listarPorCliente(int idCliente) throws SQLException {
        String sql = "SELECT * FROM vw_Contas WHERE id_cliente = ?";
        List<Conta> lista = new ArrayList<>();

        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearConta(rs));
            }
            return lista;

        } finally {
            ConnectionFactory.fecharConexao(conn);
        }
    }

    // ── Atualizar saldo (usado internamente pelo service) ──
    public void atualizarSaldo(int idConta, java.math.BigDecimal novoSaldo,
                               Connection conn) throws SQLException {
        String sql = "UPDATE Conta SET saldo = ? WHERE id_conta = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, novoSaldo);
            ps.setInt(2, idConta);
            ps.executeUpdate();
        }
        // Atenção: conn NÃO é fechada aqui — quem controla é o Service (por causa do commit/rollback)
    }

    // ── Buscar por ID (conexão externa — para transações) ─
    public Conta buscarPorId(int idConta, Connection conn) throws SQLException {
        String sql = "SELECT * FROM vw_Contas WHERE id_conta = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapearConta(rs);
            return null;
        }
    }

    // ── Gerar próximo número de conta ─────────────────
    public String gerarNumeroConta() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Conta";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            int total = rs.getInt(1) + 1;
            return String.format("%04d-%d", total, total % 10);
        } finally {
            ConnectionFactory.fecharConexao(conn);
        }
    }

    // ── Mapeia ResultSet → objeto Conta ───────────────
    private Conta mapearConta(ResultSet rs) throws SQLException {
        Conta c = new Conta();
        c.setIdConta(rs.getInt("id_conta"));
        c.setNumeroConta(rs.getString("numero_conta"));
        c.setNomeCliente(rs.getString("nome_cliente"));
        c.setTipoConta(rs.getString("tipo_conta"));
        c.setSaldo(rs.getBigDecimal("saldo"));
        c.setAtiva(rs.getBoolean("ativa"));
        return c;
    }
}
