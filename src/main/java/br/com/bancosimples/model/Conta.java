package br.com.bancosimples.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa uma conta bancária.
 * Espelha a tabela Conta do SQL Server.
 */
public class Conta {

    private int        idConta;
    private String     numeroConta;
    private int        idCliente;
    private String     nomeCliente;      // vem da view vw_Contas
    private int        idTipo;
    private String     tipoConta;        // "Corrente" ou "Poupança"
    private BigDecimal saldo;
    private boolean    ativa;
    private LocalDateTime dataAbertura;

    // ── Construtores ──────────────────────────────────

    public Conta() {}

    public Conta(String numeroConta, int idCliente, int idTipo) {
        this.numeroConta = numeroConta;
        this.idCliente   = idCliente;
        this.idTipo      = idTipo;
        this.saldo       = BigDecimal.ZERO;
        this.ativa       = true;
    }

    // ── Getters e Setters ─────────────────────────────

    public int        getIdConta()                      { return idConta; }
    public void       setIdConta(int idConta)           { this.idConta = idConta; }

    public String     getNumeroConta()                  { return numeroConta; }
    public void       setNumeroConta(String n)          { this.numeroConta = n; }

    public int        getIdCliente()                    { return idCliente; }
    public void       setIdCliente(int id)              { this.idCliente = id; }

    public String     getNomeCliente()                  { return nomeCliente; }
    public void       setNomeCliente(String nome)       { this.nomeCliente = nome; }

    public int        getIdTipo()                       { return idTipo; }
    public void       setIdTipo(int idTipo)             { this.idTipo = idTipo; }

    public String     getTipoConta()                    { return tipoConta; }
    public void       setTipoConta(String tipo)         { this.tipoConta = tipo; }

    public BigDecimal getSaldo()                        { return saldo; }
    public void       setSaldo(BigDecimal saldo)        { this.saldo = saldo; }

    public boolean    isAtiva()                         { return ativa; }
    public void       setAtiva(boolean ativa)           { this.ativa = ativa; }

    public LocalDateTime getDataAbertura()              { return dataAbertura; }
    public void          setDataAbertura(LocalDateTime d) { this.dataAbertura = d; }

    @Override
    public String toString() {
        return "Conta{numero='" + numeroConta + "', tipo='" + tipoConta
             + "', saldo=R$" + saldo + ", ativa=" + ativa + "}";
    }
}
