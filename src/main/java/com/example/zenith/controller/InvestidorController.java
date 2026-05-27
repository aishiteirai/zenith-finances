package com.example.zenith.controller;

import com.example.zenith.model.Investidor;
import com.example.zenith.repository.InvestidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/investidor")
public class InvestidorController {

    @Autowired
    private InvestidorRepository investidorRepository;

    @PostMapping("/aporte-global")
    public ResponseEntity<Map<String, String>> realizarAporteGlobal(@RequestBody Map<String, BigDecimal> payload,
                                                                    @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, String> response = new HashMap<>();
        BigDecimal valor = payload.get("valor");

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            response.put("erro", "Valor inválido para depósito.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Investidor investidor = investidorRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        investidor.setSaldoGlobal(investidor.getSaldoGlobal().add(valor));
        investidorRepository.save(investidor);

        response.put("mensagem", "Aporte global realizado com sucesso.");
        return ResponseEntity.ok(response);
    }
}