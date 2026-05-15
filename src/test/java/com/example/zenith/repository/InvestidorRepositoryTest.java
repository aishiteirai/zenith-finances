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
        Investidor inv = new Investidor();
        inv.setNome("Vitalik");
        inv.setEmail("vitalik@zenith.node");
        inv.setSenhaHash("hashQualquer");
        entityManager.persist(inv);
        entityManager.flush();

        Optional<Investidor> encontrado = investidorRepository.findByEmail("vitalik@zenith.node");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Vitalik");
    }

    @Test
    void deveRetornarVazioSeEmailNaoExistir() {
        Optional<Investidor> encontrado = investidorRepository.findByEmail("inexistente@zenith.node");

        assertThat(encontrado).isEmpty();
    }
}