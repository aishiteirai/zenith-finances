package com.example.zenith.repository;

import com.example.zenith.model.Investidor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InvestidorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InvestidorRepository investidorRepository;

    @Test
    void deveEncontrarInvestidorPorEmail() {
        // Arrange: Criar e persistir um investidor na base de dados de teste
        Investidor inv = new Investidor();
        inv.setNome("Vitalik");
        inv.setEmail("vitalik@zenith.node");
        inv.setSenhaHash("hashQualquer");
        entityManager.persist(inv);
        entityManager.flush();

        // Act
        Optional<Investidor> encontrado = investidorRepository.findByEmail("vitalik@zenith.node");

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Vitalik");
    }

    @Test
    void deveRetornarVazioSeEmailNaoExistir() {
        // Act
        Optional<Investidor> encontrado = investidorRepository.findByEmail("inexistente@zenith.node");

        // Assert
        assertThat(encontrado).isEmpty();
    }
}