package com.example.zenith.config;

import com.example.zenith.model.Ativo;
import com.example.zenith.repository.AtivoRepository;
import com.example.zenith.service.CotacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CotacaoScheduler {

    @Autowired private AtivoRepository ativoRepository;
    @Autowired private CotacaoService cotacaoService;

    // Roda a cada 5 minutos (300.000 ms)
    @Scheduled(fixedDelay = 300000)
    public void atualizarCotas() {
        List<Ativo> ativos = ativoRepository.findAll();
        for (Ativo ativo : ativos) {
            var preco = cotacaoService.buscarPrecoAtual(ativo.getTicker());
            if (preco.signum() > 0) {
                ativo.setPrecoAtual(preco);
                ativoRepository.save(ativo);
            }
        }
    }
}