package com.example.zenith.controller;

import com.example.zenith.dto.TransacaoRequestDTO;
import com.example.zenith.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @PostMapping
    public ResponseEntity<?> registrarTransacao(
            @RequestBody TransacaoRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // CORREÇÃO: Chama o novo método seguro, injetando a identidade real do usuário (Anti-IDOR)
            transacaoService.executarTransacao(request, userDetails.getUsername());

            return ResponseEntity.ok().body("{\"mensagem\": \"Transação executada com sucesso.\"}");
        } catch (SecurityException e) {
            // Tratamento específico para tentativa de invasão (IDOR)
            return ResponseEntity.status(403).body("{\"erro\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            // Tratamento genérico (saldo insuficiente, etc)
            return ResponseEntity.badRequest().body("{\"erro\": \"" + e.getMessage() + "\"}");
        }
    }
}