package com.example.zenith.repository;

import com.example.zenith.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    // O Spring JPA gera a query SQL automaticamente lendo o nome do método:
    // "Encontre Transações onde a Carteira pertence ao Investidor com este Email, ordenado pela Data Decrescente"
    List<Transacao> findByCarteiraInvestidorEmailOrderByDataOperacaoDesc(String email);
}