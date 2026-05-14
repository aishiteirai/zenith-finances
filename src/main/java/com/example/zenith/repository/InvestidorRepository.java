package com.example.zenith.repository;

import com.example.zenith.model.Investidor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvestidorRepository extends JpaRepository<Investidor, Long> {
    // Método mágico do Spring que procura na base de dados se o e-mail já existe
    Optional<Investidor> findByEmail(String email);
}