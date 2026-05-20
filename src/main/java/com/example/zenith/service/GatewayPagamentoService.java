package com.example.zenith.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class GatewayPagamentoService {

    // Simula a comunicação com um Gateway de Pagamentos (PIX/TED)
    public boolean processarPagamento(BigDecimal valor) {
        // Regra fictícia para teste: Rejeita depósitos menores que R$ 50,00
        if (valor == null || valor.compareTo(new BigDecimal("50.00")) < 0) {
            return false;
        }
        // Simula o tempo de rede
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        return true; // Pagamento Confirmado
    }
}