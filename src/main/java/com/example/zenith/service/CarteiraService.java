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

    public List<Carteira> listarCarteiras(String email) {
        return carteiraRepository.findByInvestidorEmail(email);
    }

    public Carteira buscarCarteiraPorIdEInvestidor(Long id, String email) {
        return carteiraRepository.findByIdAndInvestidorEmail(id, email)
                .orElseThrow(() -> new RuntimeException("Ambiente de carteira não localizado."));
    }

    @Transactional
    public Carteira criarCarteira(String email, String nome, BigDecimal valorInicial) {
        Investidor investidor = investidorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Investidor não encontrado."));

        // Regra de validação: Criação consome do saldo em inércia
        if (investidor.getSaldoGlobal().compareTo(valorInicial) < 0) {
            throw new RuntimeException("Deploy recusado: Saldo em inércia insuficiente no cofre global.");
        }

        investidor.setSaldoGlobal(investidor.getSaldoGlobal().subtract(valorInicial));
        investidorRepository.save(investidor);

        Carteira carteira = new Carteira();
        carteira.setNome(nome);
        carteira.setSaldoDisponivel(valorInicial);
        carteira.setDataCriacao(LocalDate.now());
        carteira.setInvestidor(investidor);

        return carteiraRepository.save(carteira);
    }

    @Transactional
    public Carteira processarAporte(Long carteiraId, BigDecimal valor, String email) {
        Carteira carteira = buscarCarteiraPorIdEInvestidor(carteiraId, email);
        Investidor investidor = carteira.getInvestidor();

        if (investidor.getSaldoGlobal().compareTo(valor) < 0) {
            throw new RuntimeException("Aporte recusado: Saldo em inércia insuficiente.");
        }

        investidor.setSaldoGlobal(investidor.getSaldoGlobal().subtract(valor));
        carteira.setSaldoDisponivel(carteira.getSaldoDisponivel().add(valor));

        investidorRepository.save(investidor);
        return carteiraRepository.save(carteira);
    }

    @Transactional
    public Carteira processarSaque(Long carteiraId, BigDecimal valor, String email) {
        Carteira carteira = buscarCarteiraPorIdEInvestidor(carteiraId, email);
        Investidor investidor = carteira.getInvestidor();

        if (carteira.getSaldoDisponivel().compareTo(valor) < 0) {
            throw new RuntimeException("Resgate recusado: Recursos indisponíveis na carteira.");
        }

        carteira.setSaldoDisponivel(carteira.getSaldoDisponivel().subtract(valor));
        investidor.setSaldoGlobal(investidor.getSaldoGlobal().add(valor)); // Soma de volta ao saldo de inércia

        investidorRepository.save(investidor);
        return carteiraRepository.save(carteira);
    }
}