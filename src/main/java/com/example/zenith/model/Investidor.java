package com.example.zenith.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @OneToMany(mappedBy = "investidor", cascade = CascadeType.ALL)
    private List<Carteira> carteiras;

    @Column(precision = 19, scale = 2)
    private BigDecimal saldoGlobal = BigDecimal.ZERO; // Saldo em inércia (não investido)

    public void adicionarSaldo(BigDecimal valor) {
        this.saldoGlobal = this.saldoGlobal.add(valor);
    }

    public void deduzirSaldo(BigDecimal valor) {
        if (valor.compareTo(this.saldoGlobal) > 0) throw new RuntimeException("Saldo insuficiente no cofre global.");
        this.saldoGlobal = this.saldoGlobal.subtract(valor);
    }
}