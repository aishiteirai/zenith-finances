package com.example.zenith.repository;

import com.example.zenith.model.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtivoRepository extends JpaRepository<Ativo, Long> {

    // CORRETO: Sem a palavra 'static'
    List<Ativo> findByCategoria(String categoria);

}