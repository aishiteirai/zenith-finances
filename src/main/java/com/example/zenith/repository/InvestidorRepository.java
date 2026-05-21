package com.example.zenith.repository;

import com.example.zenith.model.Investidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestidorRepository extends JpaRepository<Investidor, Long> {

    // CORRETO: Sem a palavra 'static'
    Optional<Investidor> findByEmail(String email);

}