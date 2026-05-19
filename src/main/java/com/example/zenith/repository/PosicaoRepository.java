package com.example.zenith.repository;
import com.example.zenith.model.Posicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PosicaoRepository extends JpaRepository<Posicao, Long> {
    Optional<Posicao> findByCarteiraIdAndAtivoId(Long carteiraId, Long ativoId);
}