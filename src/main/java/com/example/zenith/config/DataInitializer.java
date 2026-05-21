package com.example.zenith.config;

import com.example.zenith.model.Ativo;
import com.example.zenith.model.Investidor;
import com.example.zenith.repository.AtivoRepository;
import com.example.zenith.repository.InvestidorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner inicializarBancoDeDados(AtivoRepository ativoRepository,
                                                     InvestidorRepository investidorRepository,
                                                     PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Criar Investidor/Usuário Padrão se não existir
            String emailPadrao = "ryan@teste.com";
            if (investidorRepository.findByEmail(emailPadrao).isEmpty()) {
                Investidor investidorPadrao = new Investidor();
                investidorPadrao.setNome("Satoshi Zenith");
                investidorPadrao.setEmail(emailPadrao);
                // Criptografa a senha usando o algoritmo BCrypt definido no Security.java
                investidorPadrao.setSenhaHash(passwordEncoder.encode("testetesteteste"));

                investidorRepository.save(investidorPadrao);
                System.out.println("[ZENITH] Usuário padrão criado");
            }

            // 2. Criar Ativos se o catálogo estiver vazio
            if (ativoRepository.count() == 0) {
                Ativo cdb = new Ativo();
                cdb.setTicker("CDB-ITAU-2027");
                cdb.setNomeEmpresa("Banco Itaú");
                cdb.setCategoria("RENDA_FIXA");
                cdb.setTaxaRendimentoEstimada(new BigDecimal("110.00"));
                cdb.setTipoRentabilidade("CDI");

                Ativo tesouro = new Ativo();
                tesouro.setTicker("TESOURO-PRE-2029");
                tesouro.setNomeEmpresa("Tesouro Nacional");
                tesouro.setCategoria("RENDA_FIXA");
                tesouro.setTaxaRendimentoEstimada(new BigDecimal("10.50"));
                tesouro.setTipoRentabilidade("PREFIXADO");

                Ativo btc = new Ativo();
                btc.setTicker("BTC/BRL");
                btc.setNomeEmpresa("Bitcoin");
                btc.setCategoria("CRIPTO");
                btc.setTaxaRendimentoEstimada(new BigDecimal("0.00"));
                btc.setTipoRentabilidade("VARIAVEL");

                Ativo petr4 = new Ativo();
                petr4.setTicker("PETR4");
                petr4.setNomeEmpresa("Petrobras");
                petr4.setCategoria("ACAO");
                petr4.setTaxaRendimentoEstimada(new BigDecimal("15.20"));
                petr4.setTipoRentabilidade("VARIAVEL");

                ativoRepository.saveAll(Arrays.asList(cdb, tesouro, btc, petr4));
                System.out.println("[ZENITH] Catálogo de ativos semeado com sucesso.");
            }
        };
    }
}