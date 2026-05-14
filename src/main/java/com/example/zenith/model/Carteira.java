package com.example.zenith.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Carteira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private BigDecimal saldoDisponivel = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate dataCriacao = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investidor_id", nullable = false)
    private Investidor investidor;

    @OneToMany(mappedBy = "carteira", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Posicao> posicoes;

    @OneToMany(mappedBy = "carteira", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transacao> transacoes;

    public Carteira() {}

    // Métodos de Negócio exigidos no UML
    public void adicionarSaldo(BigDecimal valor) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
            this.saldoDisponivel = this.saldoDisponivel.add(valor);
        }
    }

    public void deduzirSaldo(BigDecimal valor) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
            this.saldoDisponivel = this.saldoDisponivel.subtract(valor);
        }
    }

    public boolean verificarSaldoSuficiente(BigDecimal valorRequerido) {
        return this.saldoDisponivel.compareTo(valorRequerido) >= 0;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getSaldoDisponivel() { return saldoDisponivel; }
    public void setSaldoDisponivel(BigDecimal saldoDisponivel) { this.saldoDisponivel = saldoDisponivel; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }
    public Investidor getInvestidor() { return investidor; }
    public void setInvestidor(Investidor investidor) { this.investidor = investidor; }
    public List<Posicao> getPosicoes() { return posicoes; }
    public void setPosicoes(List<Posicao> posicoes) { this.posicoes = posicoes; }
    public List<Transacao> getTransacoes() { return transacoes; }
    public void setTransacoes(List<Transacao> transacoes) { this.transacoes = transacoes; }
}