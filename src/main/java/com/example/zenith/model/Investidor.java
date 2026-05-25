package com.example.zenith.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Investidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    @Column(precision = 19, scale = 2)
    private BigDecimal saldoGlobal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "investidor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Carteira> carteiras;

    @Column(nullable = false)
    private String role = "ROLE_USER";

    @Column(nullable = false)
    private boolean bloqueado = false;
}