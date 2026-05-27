package com.example.zenith.controller;

import com.example.zenith.model.Carteira;
import com.example.zenith.model.Posicao;
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

    @PostMapping("/carteiras/nova")
    public String criarNovaCarteira(@RequestParam String nome,
                                    @RequestParam BigDecimal valorAporte,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        try {
            carteiraService.criarCarteira(userDetails.getUsername(), nome, valorAporte);
            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erroCriacao", e.getMessage());
            return "redirect:/home";
        }
    }

    @GetMapping("/carteiras/{id}")
    public String detalhesCarteira(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Carteira carteira = carteiraService.buscarCarteiraPorIdEInvestidor(id, userDetails.getUsername());

            BigDecimal totalInvestido = BigDecimal.ZERO;
            BigDecimal totalRendimentoPonderado = BigDecimal.ZERO;

            for (Posicao pos : carteira.getPosicoes()) {
                if (pos.getQuantidadeAtual() > 0) {
                    BigDecimal valorPosicao = pos.getPrecoMedio().multiply(new BigDecimal(pos.getQuantidadeAtual()));
                    totalInvestido = totalInvestido.add(valorPosicao);

                    if (pos.getAtivo().getTaxaRendimentoEstimada() != null) {
                        totalRendimentoPonderado = totalRendimentoPonderado.add(
                                valorPosicao.multiply(pos.getAtivo().getTaxaRendimentoEstimada())
                        );
                    }
                }
            }

            BigDecimal rendimentoMedio = BigDecimal.ZERO;
            if (totalInvestido.compareTo(BigDecimal.ZERO) > 0) {
                rendimentoMedio = totalRendimentoPonderado.divide(totalInvestido, 2, java.math.RoundingMode.HALF_UP);
            }

            model.addAttribute("carteira", carteira);
            model.addAttribute("ativos", ativoRepository.findByVisivelTrue());
            model.addAttribute("rendimentoMedio", rendimentoMedio);
            model.addAttribute("totalInvestido", totalInvestido);

            return "carteira-detalhes";
        } catch (Exception e) {
            return "redirect:/home";
        }
    }

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

    @PostMapping("/api/carteiras/{id}/saque")
    @ResponseBody
    public ResponseEntity<Map<String, String>> realizarSaque(@PathVariable Long id,
                                                             @RequestBody Map<String, BigDecimal> payload,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, String> response = new HashMap<>();
        try {
            carteiraService.processarSaque(id, payload.get("valor"), userDetails.getUsername());
            response.put("mensagem", "Saque processado com sucesso!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}