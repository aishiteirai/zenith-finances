package com.example.zenith.config;

import com.example.zenith.model.*;
import com.example.zenith.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private InvestidorRepository investidorRepository;
    @Autowired private CarteiraRepository carteiraRepository;
    @Autowired private AtivoRepository ativoRepository;
    @Autowired private TransacaoRepository transacaoRepository;
    @Autowired private PosicaoRepository posicaoRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se o banco já foi populado
        if (investidorRepository.count() > 0) {
            System.out.println("[ZENITH SYSTEM] Banco de dados persistente detectado. Puxando dados existentes...");
            return;
        }

        System.out.println("[ZENITH SYSTEM] Inicializando novo banco de dados. Semeando dados históricos...");

        // 1. Criação do Admin
        Investidor admin = new Investidor();
        admin.setNome("Master Node Admin");
        admin.setEmail("admin@zenith.node");
        admin.setSenhaHash(passwordEncoder.encode("admin1234")); // Temporário até aplicar a correção do review
        admin.setRole("ROLE_ADMIN");
        admin.setSaldoGlobal(BigDecimal.ZERO);
        admin.setBloqueado(false);
        investidorRepository.save(admin);

        // 2. Criação de Usuários Secundários
        Investidor user2 = new Investidor();
        user2.setNome("Elena Fisher");
        user2.setEmail("elena@zenith.node");
        user2.setSenhaHash(passwordEncoder.encode("senha123"));
        user2.setRole("ROLE_USER");
        user2.setSaldoGlobal(new BigDecimal("150000.00"));
        user2.setBloqueado(false);
        investidorRepository.save(user2);

        // 3. Criação do Usuário Principal
        Investidor user = new Investidor();
        user.setNome("Satoshi Nakamoto");
        user.setEmail("user@zenith.node");
        user.setSenhaHash(passwordEncoder.encode("SenhaForte1234"));
        user.setRole("ROLE_USER");
        user.setSaldoGlobal(new BigDecimal("50000.00"));
        user.setBloqueado(false);
        investidorRepository.save(user);

        // 4. Catálogo de Ativos Expandido usando o Helper Method seguro
        Ativo btc = criarAtivo("BTC/BRL", "Bitcoin", "CRIPTO", "45.5", "VARIAVEL", "0", true);
        Ativo eth = criarAtivo("ETH/BRL", "Ethereum", "CRIPTO", "32.1", "VARIAVEL", "0", true);
        Ativo sol = criarAtivo("SOL/BRL", "Solana", "CRIPTO", "60.0", "VARIAVEL", "0", true);

        Ativo petr4 = criarAtivo("PETR4", "Petrobras PN", "ACAO", "12.4", "DIVIDENDOS", "35.00", true);
        Ativo vale3 = criarAtivo("VALE3", "Vale ON", "ACAO", "9.2", "DIVIDENDOS", "60.00", true);
        Ativo wege3 = criarAtivo("WEGE3", "WEG SA", "ACAO", "15.8", "CRESCIMENTO", "40.00", true);
        Ativo ivvb11 = criarAtivo("IVVB11", "iShares S&P 500", "ACAO", "18.5", "ETF", "280.00", true);

        Ativo mxrf11 = criarAtivo("MXRF11", "Maxi Renda FII", "FII", "11.5", "RENDIMENTO_MENSAL", "10.50", true);
        Ativo hglg11 = criarAtivo("HGLG11", "CSHG Logística", "FII", "9.8", "RENDIMENTO_MENSAL", "165.00", true);

        Ativo cdbItau = criarAtivo("CDB-ITAU", "Banco Itaú S.A.", "RENDA_FIXA", "11.15", "CDI", "100.00", true);
        Ativo tesouroSelic = criarAtivo("TD-SELIC", "Tesouro Nacional", "RENDA_FIXA", "10.5", "SELIC", "130.00", true);

        ativoRepository.saveAll(List.of(btc, eth, sol, petr4, vale3, wege3, ivvb11, mxrf11, hglg11, cdbItau, tesouroSelic));

        // 5. Estruturas (Carteiras) usando o Helper Method seguro
        Carteira cartCripto = criarCarteira(user, "Cold Wallet Cripto", "1500000.00"); // 1.5 Milhão
        Carteira cartAcoes = criarCarteira(user, "Ações Longo Prazo", "500000.00");
        Carteira cartRendaFixa = criarCarteira(user, "Reserva de Oportunidade", "800000.00");
        carteiraRepository.saveAll(List.of(cartCripto, cartAcoes, cartRendaFixa));

        // 6. Gerador Temporal Seguro de Transações
        LocalDateTime hoje = LocalDateTime.now();

        realizarOperacao(cartCripto, btc, TipoTransacao.COMPRA, 2, new BigDecimal("250000.00"), hoje.minusMonths(5).minusDays(10));
        realizarOperacao(cartAcoes, petr4, TipoTransacao.COMPRA, 500, new BigDecimal("32.50"), hoje.minusMonths(5).minusDays(5));

        realizarOperacao(cartAcoes, vale3, TipoTransacao.COMPRA, 200, new BigDecimal("65.00"), hoje.minusMonths(4).minusDays(15));
        realizarOperacao(cartCripto, eth, TipoTransacao.COMPRA, 5, new BigDecimal("15000.00"), hoje.minusMonths(4).minusDays(2));

        realizarOperacao(cartRendaFixa, tesouroSelic, TipoTransacao.COMPRA, 100, new BigDecimal("130.00"), hoje.minusMonths(3).minusDays(20));
        realizarOperacao(cartAcoes, ivvb11, TipoTransacao.COMPRA, 50, new BigDecimal("250.00"), hoje.minusMonths(3).minusDays(8));

        realizarOperacao(cartAcoes, wege3, TipoTransacao.COMPRA, 300, new BigDecimal("38.00"), hoje.minusMonths(2).minusDays(12));
        realizarOperacao(cartAcoes, petr4, TipoTransacao.VENDA, 100, new BigDecimal("39.00"), hoje.minusMonths(2).minusDays(5));

        realizarOperacao(cartRendaFixa, cdbItau, TipoTransacao.COMPRA, 200, new BigDecimal("100.00"), hoje.minusMonths(1).minusDays(18));
        realizarOperacao(cartAcoes, hglg11, TipoTransacao.COMPRA, 100, new BigDecimal("160.00"), hoje.minusMonths(1).minusDays(10));

        realizarOperacao(cartCripto, btc, TipoTransacao.COMPRA, 1, new BigDecimal("360000.00"), hoje.minusDays(2));

        System.out.println("[ZENITH SYSTEM] Dados simulados com sucesso.");
    }

    // ==============================================================
    // HELPER METHODS (Constroem os objetos de forma segura via Setters)
    // ==============================================================

    private Ativo criarAtivo(String ticker, String nome, String cat, String taxa, String tipo, String min, boolean visivel) {
        Ativo a = new Ativo();
        a.setTicker(ticker);
        a.setNomeEmpresa(nome);
        a.setCategoria(cat);
        a.setTaxaRendimentoEstimada(new BigDecimal(taxa));
        a.setTipoRentabilidade(tipo);
        a.setValorMinimo(new BigDecimal(min));
        a.setVisivel(visivel);
        return a;
    }

    private Carteira criarCarteira(Investidor inv, String nome, String saldo) {
        Carteira c = new Carteira();
        c.setInvestidor(inv);
        c.setNome(nome);
        c.setSaldoDisponivel(new BigDecimal(saldo));
        // Se a sua carteira tiver dataCriacao ou outros campos, defina aqui.
        return c;
    }

    private void realizarOperacao(Carteira carteiraParam, Ativo ativoParam, TipoTransacao tipo, int qtd, BigDecimal preco, LocalDateTime data) {
        // 1. RE-FETCH: Busca os objetos "frescos" e anexados ao Hibernate para evitar o erro de Orphan Collection
        Carteira c = carteiraRepository.findById(carteiraParam.getId()).orElseThrow();
        Ativo a = ativoRepository.findById(ativoParam.getId()).orElseThrow();

        // 2. Agora usamos as variáveis 'c' e 'a' seguras para o resto da lógica
        Transacao tx = new Transacao();
        tx.setCarteira(c);
        tx.setAtivo(a);
        tx.setTipo(tipo);
        tx.setQuantidade(qtd);
        tx.setPrecoUnitario(preco);
        tx.setDataOperacao(data);
        transacaoRepository.save(tx);

        BigDecimal valorTotal = preco.multiply(new BigDecimal(qtd));

        if (tipo == TipoTransacao.COMPRA) {
            c.setSaldoDisponivel(c.getSaldoDisponivel().subtract(valorTotal));
        } else {
            c.setSaldoDisponivel(c.getSaldoDisponivel().add(valorTotal));
        }
        carteiraRepository.save(c);

        Posicao p = posicaoRepository.findByCarteiraIdAndAtivoId(c.getId(), a.getId()).orElseGet(() -> {
            Posicao nova = new Posicao();
            nova.setCarteira(c);
            nova.setAtivo(a);
            nova.setQuantidadeAtual(0);
            nova.setPrecoMedio(BigDecimal.ZERO);
            return nova;
        });

        if (tipo == TipoTransacao.COMPRA) {
            int novaQtd = p.getQuantidadeAtual() + qtd;
            BigDecimal totalAtual = p.getPrecoMedio().multiply(new BigDecimal(p.getQuantidadeAtual()));
            BigDecimal novoPrecoMedio = totalAtual.add(valorTotal).divide(new BigDecimal(novaQtd), 6, RoundingMode.HALF_UP);

            p.setQuantidadeAtual(novaQtd);
            p.setPrecoMedio(novoPrecoMedio);
        } else {
            p.setQuantidadeAtual(p.getQuantidadeAtual() - qtd);
        }

        posicaoRepository.save(p);
    }
}