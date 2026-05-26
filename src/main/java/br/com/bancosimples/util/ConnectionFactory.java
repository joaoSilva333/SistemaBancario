package br.com.bancosimples.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Responsável por criar e fechar conexões com o SQL Server.
 */
public class ConnectionFactory {

    private static final String BANCO   = "SistemaBancario";
    private static final String USUARIO = "sa";
    private static final String SENHA   = "sua_senha_aqui";

    private static final String URL =
            "jdbc:sqlserver://localhost\\SQLEXPRESS"
                    + ";databaseName=" + BANCO
                    + ";encrypt=false"
                    + ";trustServerCertificate=true";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQL Server não encontrado. Adicione o .jar ao projeto.", e);
        }
    }

    public static void fecharConexao(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}