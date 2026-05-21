package com.example.zenith.service;

import com.example.zenith.model.Carteira;
import com.example.zenith.model.Investidor;
import com.example.zenith.repository.CarteiraRepository;
import com.example.zenith.repository.InvestidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CarteiraService {

    @Autowired private CarteiraRepository carteiraRepository;
    @Autowired private InvestidorRepository investidorRepository;
    @Autowired private GatewayPagamentoService gatewayPagamento;

    public List<Carteira> listarCarteiras(String emailInvestidor) {
        return carteiraRepository.findByInvestidorEmail(emailInvestidor);
    }

    public Carteira buscarCarteiraPorIdEInvestidor(Long carteiraId, String emailInvestidor) {
        Carteira carteira = carteiraRepository.findById(carteiraId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada."));

        if (!carteira.getInvestidor().getEmail().equals(emailInvestidor)) {
            throw new RuntimeException("Acesso negado a esta carteira.");
        }
        return carteira;
    }

    @Transactional
    public Carteira criarCarteira(String emailInvestidor, String nomeCarteira, BigDecimal saldoInicial) {
        // REGRA: A carteira só poderá ser criada se houver saldo e for aprovado no gateway
        if (saldoInicial == null || saldoInicial.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("É necessário um aporte inicial obrigatório para criar a carteira.");
        }

        if (!gatewayPagamento.processarPagamento(saldoInicial)) {
            throw new RuntimeException("Falha no pagamento do aporte inicial. Carteira não criada.");
        }

        Investidor investidor = investidorRepository.findByEmail(emailInvestidor)
                .orElseThrow(() -> new RuntimeException("Investidor não encontrado."));

        Carteira novaCarteira = new Carteira();
        novaCarteira.setNome(nomeCarteira);
        novaCarteira.setSaldoDisponivel(saldoInicial);
        novaCarteira.setDataCriacao(LocalDate.now());
        novaCarteira.setInvestidor(investidor);

        return carteiraRepository.save(novaCarteira);
    }

    @Transactional
    public Carteira processarAporte(Long carteiraId, BigDecimal valor, String emailInvestidor) {
        Carteira carteira = buscarCarteiraPorIdEInvestidor(carteiraId, emailInvestidor);

        // Comunicação com o Gateway
        if (!gatewayPagamento.processarPagamento(valor)) {
            throw new RuntimeException("Transferência rejeitada pelo banco ou valor mínimo não atingido (R$ 50).");
        }

        // Atualiza a Carteira
        carteira.adicionarSaldo(valor);
        return carteiraRepository.save(carteira);
    }

    @Transactional
    public Carteira processarSaque(Long carteiraId, BigDecimal valor, String emailInvestidor) {
        Carteira carteira = buscarCarteiraPorIdEInvestidor(carteiraId, emailInvestidor);

        // Verifica se há saldo suficiente antes de enviar ao Gateway
        if (!carteira.verificarSaldoSuficiente(valor)) {
            throw new RuntimeException("Saldo insuficiente para realizar este saque.");
        }

        // Comunicação com o Gateway
        if (!gatewayPagamento.processarPagamento(valor)) {
            throw new RuntimeException("Saque rejeitado pelo banco ou valor mínimo não atingido (R$ 50).");
        }

        // Atualiza a Carteira
        carteira.deduzirSaldo(valor);
        return carteiraRepository.save(carteira);
    }
}