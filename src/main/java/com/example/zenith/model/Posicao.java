package com.example.zenith.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
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

    public void atualizarPosicao(int quantidade, BigDecimal precoCotacao, TipoTransacao tipo) {
        if (tipo == TipoTransacao.COMPRA) {
            BigDecimal valorTotalAntigo = this.precoMedio.multiply(new BigDecimal(this.quantidadeAtual));
            BigDecimal valorNovaCompra = precoCotacao.multiply(new BigDecimal(quantidade));
            this.quantidadeAtual += quantidade;
            this.precoMedio = valorTotalAntigo.add(valorNovaCompra).divide(new BigDecimal(this.quantidadeAtual), java.math.RoundingMode.HALF_UP);
        } else if (tipo == TipoTransacao.VENDA) {
            this.quantidadeAtual -= quantidade;
        }
    }
}