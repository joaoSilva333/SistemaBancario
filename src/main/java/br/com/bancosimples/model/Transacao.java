package br.com.bancosimples.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa uma movimentação financeira.
 * Espelha a tabela Transacao do SQL Server.
 */
public class Transacao {

    private int           idTransacao;
    private int           idConta;
    private String        numeroConta;       // vem da view
    private int           idTipo;
    private String        tipoTransacao;     // "Depósito", "Saque", etc.
    private BigDecimal    valor;
    private BigDecimal    saldoApos;
    private Integer       idContaDestino;    // nullable
    private String        contaDestino;      // vem da view
    private LocalDateTime dataTransacao;
    private String        descricao;

    // ── Construtores ──────────────────────────────────

    public Transacao() {}

    public Transacao(int idConta, int idTipo, BigDecimal valor,
                     BigDecimal saldoApos, String descricao) {
        this.idConta   = idConta;
        this.idTipo    = idTipo;
        this.valor     = valor;
        this.saldoApos = saldoApos;
        this.descricao = descricao;
    }

    // ── Getters e Setters ─────────────────────────────

    public int           getIdTransacao()               { return idTransacao; }
    public void          setIdTransacao(int id)         { this.idTransacao = id; }

    public int           getIdConta()                   { return idConta; }
    public void          setIdConta(int id)             { this.idConta = id; }

    public String        getNumeroConta()               { return numeroConta; }
    public void          setNumeroConta(String n)       { this.numeroConta = n; }

    public int           getIdTipo()                    { return idTipo; }
    public void          setIdTipo(int id)              { this.idTipo = id; }

    public String        getTipoTransacao()             { return tipoTransacao; }
    public void          setTipoTransacao(String t)     { this.tipoTransacao = t; }

    public BigDecimal    getValor()                     { return valor; }
    public void          setValor(BigDecimal valor)     { this.valor = valor; }

    public BigDecimal    getSaldoApos()                 { return saldoApos; }
    public void          setSaldoApos(BigDecimal s)     { this.saldoApos = s; }

    public Integer       getIdContaDestino()            { return idContaDestino; }
    public void          setIdContaDestino(Integer id)  { this.idContaDestino = id; }

    public String        getContaDestino()              { return contaDestino; }
    public void          setContaDestino(String c)      { this.contaDestino = c; }

    public LocalDateTime getDataTransacao()             { return dataTransacao; }
    public void          setDataTransacao(LocalDateTime d) { this.dataTransacao = d; }

    public String        getDescricao()                 { return descricao; }
    public void          setDescricao(String d)         { this.descricao = d; }

    @Override
    public String toString() {
        return String.format("[%s] %s - R$ %.2f | Saldo após: R$ %.2f",
            dataTransacao, tipoTransacao, valor, saldoApos);
    }
}
