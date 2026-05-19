package com.example.zenith.repository;

import com.example.zenith.model.Carteira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarteiraRepository extends JpaRepository<Carteira, Long> {
    List<Carteira> findByInvestidorEmail(String email);
}