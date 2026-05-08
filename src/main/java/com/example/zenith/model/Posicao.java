package com.example.zenith.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Posicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carteira_id", nullable = false)
    private Carteira carteira;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ativo_id", nullable = false)
    private Ativo ativo;

    @Column(nullable = false)
    private int quantidadeAtual = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoMedio = BigDecimal.ZERO;

    public Posicao() {}

    // Método de Negócio (a lógica exata do preço médio pode ser refinada no Service)
    public void atualizarPosicao(int quantidade, BigDecimal precoCotacao, TipoTransacao tipo) {
        if (tipo == TipoTransacao.COMPRA) {
            // Lógica simplificada de preço médio
            BigDecimal valorTotalAntigo = this.precoMedio.multiply(new BigDecimal(this.quantidadeAtual));
            BigDecimal valorNovaCompra = precoCotacao.multiply(new BigDecimal(quantidade));
            this.quantidadeAtual += quantidade;
            this.precoMedio = valorTotalAntigo.add(valorNovaCompra).divide(new BigDecimal(this.quantidadeAtual), java.math.RoundingMode.HALF_UP);
        } else if (tipo == TipoTransacao.VENDA) {
            this.quantidadeAtual -= quantidade;
            // Na venda, o preço médio de aquisição não se altera
        }
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Carteira getCarteira() { return carteira; }
    public void setCarteira(Carteira carteira) { this.carteira = carteira; }
    public Ativo getAtivo() { return ativo; }
    public void setAtivo(Ativo ativo) { this.ativo = ativo; }
    public int getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(int quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }
    public BigDecimal getPrecoMedio() { return precoMedio; }
    public void setPrecoMedio(BigDecimal precoMedio) { this.precoMedio = precoMedio; }
}