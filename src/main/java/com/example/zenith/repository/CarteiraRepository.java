package com.example.zenith.repository;

import com.example.zenith.model.Carteira;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CarteiraRepository extends JpaRepository<Carteira, Long> {
    List<Carteira> findByInvestidorEmail(String email);

    Optional<Carteira> findByIdAndInvestidorEmail(Long id, String email);
}