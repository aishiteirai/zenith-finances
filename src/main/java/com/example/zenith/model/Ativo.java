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
    private String ticker;

    @Column(nullable = false)
    private String nomeEmpresa;

    @Column(nullable = false)
    private String categoria;

    @Column(precision = 10, scale = 2)
    private BigDecimal taxaRendimentoEstimada;

    @Column
    private String tipoRentabilidade;

    @Column(precision = 19, scale = 2)
    private BigDecimal valorMinimo;

    @Column(nullable = false)
    private boolean visivel = true;
}