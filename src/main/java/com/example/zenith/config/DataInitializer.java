package com.example.zenith.config;

import com.example.zenith.model.Ativo;
import com.example.zenith.repository.AtivoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner carregarAtivosIniciais(AtivoRepository ativoRepository) {
        return args -> {
            // Só insere se o banco estiver vazio
            if (ativoRepository.count() == 0) {

                // 1. Renda Fixa - Pós-fixado (CDB)
                Ativo cdb = new Ativo();
                cdb.setTicker("CDB-ITAU-2027");
                cdb.setNomeEmpresa("Banco Itaú");
                cdb.setCategoria("RENDA_FIXA");
                cdb.setTaxaRendimentoEstimada(new BigDecimal("110.00")); // 110%
                cdb.setTipoRentabilidade("CDI");

                // 2. Renda Fixa - Prefixado (Tesouro)
                Ativo tesouro = new Ativo();
                tesouro.setTicker("TESOURO-PRE-2029");
                tesouro.setNomeEmpresa("Tesouro Nacional");
                tesouro.setCategoria("RENDA_FIXA");
                tesouro.setTaxaRendimentoEstimada(new BigDecimal("10.50")); // 10,5% a.a.
                tesouro.setTipoRentabilidade("PREFIXADO");

                // 3. Renda Variável - Criptomoeda (Atualização em tempo real)
                Ativo btc = new Ativo();
                btc.setTicker("BTC/BRL");
                btc.setNomeEmpresa("Bitcoin");
                btc.setCategoria("CRIPTO");
                btc.setTaxaRendimentoEstimada(new BigDecimal("0.00")); // Depende da cotação
                btc.setTipoRentabilidade("VARIAVEL");

                // 4. Renda Variável - Ação
                Ativo petr4 = new Ativo();
                petr4.setTicker("PETR4");
                petr4.setNomeEmpresa("Petrobras");
                petr4.setCategoria("ACAO");
                petr4.setTaxaRendimentoEstimada(new BigDecimal("15.20")); // Dividend Yield projetado
                petr4.setTipoRentabilidade("VARIAVEL");

                ativoRepository.saveAll(Arrays.asList(cdb, tesouro, btc, petr4));
                System.out.println("[ZENITH] Banco de dados populado com ativos iniciais com sucesso.");
            }
        };
    }
}