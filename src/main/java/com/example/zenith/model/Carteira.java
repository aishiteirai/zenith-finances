package com.example.zenith.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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

    // Este método não cria uma coluna no banco, apenas calcula em tempo real
    @Transient
    public BigDecimal getCapitalAlocado() {
        if (this.posicoes == null || this.posicoes.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Posicao p : this.posicoes) {
            if (p.getQuantidadeAtual() > 0) {
                // Se o ativo tiver um preço de mercado atualizado pela API, usa ele. Senão, usa o preço que pagou.
                BigDecimal precoMercado = (p.getAtivo().getPrecoAtual() != null && p.getAtivo().getPrecoAtual().compareTo(BigDecimal.ZERO) > 0)
                        ? p.getAtivo().getPrecoAtual()
                        : p.getPrecoMedio();

                total = total.add(precoMercado.multiply(new BigDecimal(p.getQuantidadeAtual())));
            }
        }
        return total;
    }
}