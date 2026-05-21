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
public class Ativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticker; // Ex: AAPL, CDB-ITAU, TESOURO-SELIC

    @Column(nullable = false)
    private String nomeEmpresa;

    @Column(nullable = false)
    private String categoria; // Ex: RENDA_FIXA, CRIPTO, ACAO

    // Taxa de rendimento (Ex: 10.50 para 10,5% ao ano, ou 110 para 110% do CDI)
    @Column(precision = 10, scale = 2)
    private BigDecimal taxaRendimentoEstimada;

    // Ex: "PREFIXADO", "CDI", "IPCA+", "VARIAVEL"
    @Column
    private String tipoRentabilidade;
}