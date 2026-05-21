package com.example.zenith.config;

import com.example.zenith.model.*;
import com.example.zenith.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner carregarDadosIniciais(AtivoRepository ativoRepository,
                                                   InvestidorRepository investidorRepository,
                                                   CarteiraRepository carteiraRepository,
                                                   PosicaoRepository posicaoRepository,
                                                   TransacaoRepository transacaoRepository,
                                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (ativoRepository.count() == 0) {
                // Renda Fixa
                Ativo cdb = new Ativo();
                cdb.setTicker("CDB-ITAU-2027");
                cdb.setNomeEmpresa("Banco Itaú");
                cdb.setCategoria("RENDA_FIXA");
                cdb.setTaxaRendimentoEstimada(new BigDecimal("110.00"));
                cdb.setTipoRentabilidade("CDI");
                cdb.setValorMinimo(new BigDecimal("100.00"));

                Ativo tesouro = new Ativo();
                tesouro.setTicker("TESOURO-PRE-2029");
                tesouro.setNomeEmpresa("Tesouro Nacional");
                tesouro.setCategoria("RENDA_FIXA");
                tesouro.setTaxaRendimentoEstimada(new BigDecimal("10.50"));
                tesouro.setTipoRentabilidade("PREFIXADO");
                tesouro.setValorMinimo(new BigDecimal("30.00"));

                // Criptoativos
                Ativo btc = new Ativo();
                btc.setTicker("BTC/BRL");
                btc.setNomeEmpresa("Bitcoin");
                btc.setCategoria("CRIPTO");
                btc.setTaxaRendimentoEstimada(new BigDecimal("14.20"));
                btc.setTipoRentabilidade("VARIAVEL");
                btc.setValorMinimo(BigDecimal.ZERO); // Sem valor mínimo

                Ativo eth = new Ativo();
                eth.setTicker("ETH/BRL");
                eth.setNomeEmpresa("Ethereum");
                eth.setCategoria("CRIPTO");
                eth.setTaxaRendimentoEstimada(new BigDecimal("22.10"));
                eth.setTipoRentabilidade("VARIAVEL");
                eth.setValorMinimo(BigDecimal.ZERO);

                // Ações
                Ativo petr4 = new Ativo();
                petr4.setTicker("PETR4");
                petr4.setNomeEmpresa("Petrobras");
                petr4.setCategoria("ACAO");
                petr4.setTaxaRendimentoEstimada(new BigDecimal("15.20"));
                petr4.setTipoRentabilidade("VARIAVEL");
                petr4.setValorMinimo(new BigDecimal("50.00"));

                Ativo vale3 = new Ativo();
                vale3.setTicker("VALE3");
                vale3.setNomeEmpresa("Vale S.A.");
                vale3.setCategoria("ACAO");
                vale3.setTaxaRendimentoEstimada(new BigDecimal("11.40"));
                vale3.setTipoRentabilidade("VARIAVEL");
                vale3.setValorMinimo(new BigDecimal("70.00"));

                ativoRepository.saveAll(Arrays.asList(cdb, tesouro, btc, eth, petr4, vale3));
            }

            String emailPadrao = "user@zenith.node";
            if (investidorRepository.findByEmail(emailPadrao).isEmpty()) {
                Investidor investidor = new Investidor();
                investidor.setNome("Satoshi Zenith");
                investidor.setEmail(emailPadrao);
                investidor.setSenhaHash(passwordEncoder.encode("SenhaForte1234"));
                investidor.setSaldoGlobal(new BigDecimal("85000.00")); // Inércia Inicial
                investidor = investidorRepository.save(investidor);

                // Carteiras Iniciais
                Carteira c1 = new Carteira();
                c1.setNome("Arquitetura Alpha Growth");
                c1.setSaldoDisponivel(new BigDecimal("15000.00"));
                c1.setDataCriacao(LocalDate.now().minusMonths(1));
                c1.setInvestidor(investidor);
                carteiraRepository.save(c1);

                Carteira c2 = new Carteira();
                c2.setNome("Reserva Defensiva");
                c2.setSaldoDisponivel(new BigDecimal("10000.00"));
                c2.setDataCriacao(LocalDate.now().minusDays(5));
                c2.setInvestidor(investidor);
                carteiraRepository.save(c2);

                // Posições de Exemplo
                Ativo petr4 = ativoRepository.findAll().stream().filter(a -> a.getTicker().equals("PETR4")).findFirst().get();
                Posicao pos = new Posicao();
                pos.setCarteira(c1);
                pos.setAtivo(petr4);
                pos.setQuantidadeAtual(200);
                pos.setPrecoMedio(new BigDecimal("32.10"));
                posicaoRepository.save(pos);

                // Transação Histórica
                Transacao tx = new Transacao();
                tx.setCarteira(c1);
                tx.setAtivo(petr4);
                tx.setQuantidade(200);
                tx.setPrecoUnitario(new BigDecimal("32.10"));
                tx.setTipo(TipoTransacao.COMPRA);
                tx.setDataOperacao(LocalDateTime.now().minusDays(12));
                transacaoRepository.save(tx);

                System.out.println("[ZENITH ENGINE] Inicialização Concluída.");
            }
        };
    }
}