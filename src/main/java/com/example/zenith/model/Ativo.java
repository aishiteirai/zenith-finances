package com.example.zenith.model;

import jakarta.persistence.*;

@Entity
public class Ativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticker; // Ex: AAPL, TSLA

    @Column(nullable = false)
    private String nomeEmpresa;

    @Column(nullable = false)
    private String categoria;

    // Construtores, Getters e Setters
    public Ativo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }
    public String getNomeEmpresa() { return nomeEmpresa; }
    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}