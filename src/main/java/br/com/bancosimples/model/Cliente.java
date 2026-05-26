package br.com.bancosimples.model;

/**
 * Representa um cliente do banco.
 * Espelha a tabela Cliente do SQL Server.
 */
public class Cliente {

    private int    idCliente;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;

    // ── Construtores ──────────────────────────────────

    public Cliente() {}

    public Cliente(String nome, String cpf, String email, String telefone) {
        this.nome     = nome;
        this.cpf      = cpf;
        this.email    = email;
        this.telefone = telefone;
    }

    // ── Getters e Setters ─────────────────────────────

    public int    getIdCliente()          { return idCliente; }
    public void   setIdCliente(int id)    { this.idCliente = id; }

    public String getNome()               { return nome; }
    public void   setNome(String nome)    { this.nome = nome; }

    public String getCpf()               { return cpf; }
    public void   setCpf(String cpf)     { this.cpf = cpf; }

    public String getEmail()              { return email; }
    public void   setEmail(String email)  { this.email = email; }

    public String getTelefone()                   { return telefone; }
    public void   setTelefone(String telefone)    { this.telefone = telefone; }

    @Override
    public String toString() {
        return "Cliente{id=" + idCliente + ", nome='" + nome + "', cpf='" + cpf + "'}";
    }
}
