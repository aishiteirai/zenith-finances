package com.example.zenith.controller;

import com.example.zenith.dto.TransacaoRequestDTO;
import com.example.zenith.exception.SaldoInsuficienteException;
import com.example.zenith.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @PostMapping
    public ResponseEntity<Map<String, String>> registrarTransacao(@RequestBody TransacaoRequestDTO dto) {
        Map<String, String> response = new HashMap<>();

        try {
            // Executa o fluxo do Service
            transacaoService.registrarNovaTransacao(dto);

            // Retorna 201 Created (Sucesso)
            response.put("mensagem", "Compra realizada com sucesso!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (SaldoInsuficienteException e) {
            // Retorna 400 Bad Request (Erro de saldo)
            response.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            // Outros erros genéricos (ex: Ativo não encontrado)
            response.put("erro", "Erro interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}