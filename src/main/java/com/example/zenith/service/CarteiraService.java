package com.example.zenith.service;

import com.example.zenith.model.Carteira;
import com.example.zenith.model.Investidor;
import com.example.zenith.repository.CarteiraRepository;
import com.example.zenith.repository.InvestidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CarteiraService {

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private InvestidorRepository investidorRepository;

    public List<Carteira> listarCarteiras(String emailInvestidor) {
        return carteiraRepository.findByInvestidorEmail(emailInvestidor);
    }

    public Carteira criarCarteira(String emailInvestidor, String nomeCarteira, BigDecimal saldoInicial) {
        Investidor investidor = investidorRepository.findByEmail(emailInvestidor)
                .orElseThrow(() -> new RuntimeException("Investidor não encontrado no sistema."));

        Carteira novaCarteira = new Carteira();
        novaCarteira.setNome(nomeCarteira);
        novaCarteira.setSaldoDisponivel(saldoInicial != null ? saldoInicial : BigDecimal.ZERO);
        novaCarteira.setDataCriacao(LocalDate.now());
        novaCarteira.setInvestidor(investidor);

        return carteiraRepository.save(novaCarteira);
    }
}