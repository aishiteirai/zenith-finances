package com.example.zenith.repository;

import com.example.zenith.model.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtivoRepository extends JpaRepository<Ativo, Long> {
    List<Ativo> findByCategoria(String categoria);

    // NOVO: Busca apenas os ativos não ocultos pelo Admin
    List<Ativo> findByVisivelTrue();
}