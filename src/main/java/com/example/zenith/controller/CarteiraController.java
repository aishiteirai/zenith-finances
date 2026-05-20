package com.example.zenith.controller;

import com.example.zenith.repository.AtivoRepository;
import com.example.zenith.service.CarteiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CarteiraController {

    @Autowired private CarteiraService carteiraService;
    @Autowired private AtivoRepository ativoRepository;

    // --- ENDPOINTS DE INTERFACE (HTML) ---

    @PostMapping("/carteiras/nova")
    public String criarNovaCarteira(@RequestParam String nome,
                                    @RequestParam BigDecimal saldoInicial,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        try {
            carteiraService.criarCarteira(userDetails.getUsername(), nome, saldoInicial);
            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erroCriacao", e.getMessage());
            return "redirect:/home";
        }
    }

    // Abre a visualização "Dentro da Carteira"
    @GetMapping("/carteiras/{id}")
    public String detalhesCarteira(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            model.addAttribute("carteira", carteiraService.buscarCarteiraPorIdEInvestidor(id, userDetails.getUsername()));
            model.addAttribute("ativos", ativoRepository.findAll());
            return "carteira-detalhes";
        } catch (Exception e) {
            return "redirect:/home";
        }
    }

    // --- ENDPOINTS DE API (AJAX/Fetch) ---

    @PostMapping("/api/carteiras/{id}/aporte")
    @ResponseBody
    public ResponseEntity<Map<String, String>> realizarAporte(@PathVariable Long id,
                                                              @RequestBody Map<String, BigDecimal> payload,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, String> response = new HashMap<>();
        try {
            carteiraService.processarAporte(id, payload.get("valor"), userDetails.getUsername());
            response.put("mensagem", "Aporte realizado com sucesso!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}