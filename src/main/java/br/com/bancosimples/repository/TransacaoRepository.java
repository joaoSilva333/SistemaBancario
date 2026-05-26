package br.com.bancosimples.repository;

import br.com.bancosimples.model.Transacao;
import br.com.bancosimples.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por registrar e consultar transações no banco.
 */
public class TransacaoRepository {

    // ── Registrar uma transação (conn externa = dentro de BEGIN TRANSACTION) ──
    public void registrar(Transacao t, Connection conn) throws SQLException {
        String sql = "INSERT INTO Transacao "
                   + "(id_conta, id_tipo, valor, saldo_apos, id_conta_destino, descricao) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getIdConta());
            ps.setInt(2, t.getIdTipo());
            ps.setBigDecimal(3, t.getValor());
            ps.setBigDecimal(4, t.getSaldoApos());

            if (t.getIdContaDestino() != null) {
                ps.setInt(5, t.getIdContaDestino());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setString(6, t.getDescricao());
            ps.executeUpdate();
        }
        // conn não fechada — quem controla é o Service
    }

    // ── Buscar extrato de uma conta ───────────────────
    public List<Transacao> buscarExtrato(int idConta) throws SQLException {
        String sql = "SELECT * FROM vw_Extrato WHERE numero_conta = "
                   + "(SELECT numero_conta FROM Conta WHERE id_conta = ?) "
                   + "ORDER BY data_transacao DESC";

        List<Transacao> lista = new ArrayList<>();
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearTransacao(rs));
            }
            return lista;
        } finally {
            ConnectionFactory.fecharConexao(conn);
        }
    }

    // ── Mapeia ResultSet → objeto Transacao ───────────
    private Transacao mapearTransacao(ResultSet rs) throws SQLException {
        Transacao t = new Transacao();
        t.setIdTransacao(rs.getInt("id_transacao"));
        t.setNumeroConta(rs.getString("numero_conta"));
        t.setTipoTransacao(rs.getString("tipo_transacao"));
        t.setValor(rs.getBigDecimal("valor"));
        t.setSaldoApos(rs.getBigDecimal("saldo_apos"));
        t.setContaDestino(rs.getString("conta_destino"));
        t.setDescricao(rs.getString("observacao"));

        Timestamp ts = rs.getTimestamp("data_transacao");
        if (ts != null) t.setDataTransacao(ts.toLocalDateTime());

        return t;
    }
}
