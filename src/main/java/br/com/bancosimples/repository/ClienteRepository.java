package br.com.bancosimples.repository;

import br.com.bancosimples.model.Cliente;
import br.com.bancosimples.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por todas as operações de banco de dados da entidade Cliente.
 * Aqui ficam os SQLs — a camada de serviço não precisa saber disso.
 */
public class ClienteRepository {

    // ── Cadastrar novo cliente ─────────────────────────
    public Cliente cadastrar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO Cliente (nome, cpf, email, telefone) "
                   + "VALUES (?, ?, ?, ?)";

        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getTelefone());
            ps.executeUpdate();

            // Pega o id gerado automaticamente pelo banco
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                cliente.setIdCliente(rs.getInt(1));
            }
            return cliente;

        } finally {
            ConnectionFactory.fecharConexao(conn);
        }
    }

    // ── Buscar por CPF ────────────────────────────────
    public Cliente buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM Cliente WHERE cpf = ?";

        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cpf);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearCliente(rs);
            }
            return null; // não encontrado

        } finally {
            ConnectionFactory.fecharConexao(conn);
        }
    }

    // ── Buscar por ID ─────────────────────────────────
    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Cliente WHERE id_cliente = ?";

        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearCliente(rs);
            }
            return null;

        } finally {
            ConnectionFactory.fecharConexao(conn);
        }
    }

    // ── Listar todos ──────────────────────────────────
    public List<Cliente> listarTodos() throws SQLException {
        String sql = "SELECT * FROM Cliente ORDER BY nome";
        List<Cliente> lista = new ArrayList<>();

        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
            return lista;

        } finally {
            ConnectionFactory.fecharConexao(conn);
        }
    }

    // ── Mapeia ResultSet → objeto Cliente ─────────────
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setEmail(rs.getString("email"));
        c.setTelefone(rs.getString("telefone"));
        return c;
    }
}
