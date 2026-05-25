package com.example.zenith.controller;

import com.example.zenith.model.Carteira;
import com.example.zenith.model.Investidor;
import com.example.zenith.model.Posicao;
import com.example.zenith.repository.InvestidorRepository;
import com.example.zenith.service.CarteiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RelatorioController {

    @Autowired private CarteiraService carteiraService;
    @Autowired private InvestidorRepository investidorRepository;

    @GetMapping("/relatorios")
    public String exibirAnalytics(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String email = userDetails.getUsername();

            // Injeta o investidor para o menu unificado não falhar
            Investidor investidor = investidorRepository.findByEmail(email).orElse(null);
            model.addAttribute("investidor", investidor);

            List<Carteira> carteiras = carteiraService.listarCarteiras(email);

            BigDecimal totalCaixa = BigDecimal.ZERO;
            BigDecimal totalInvestido = BigDecimal.ZERO;

            // Estruturas limpas para enviar ao Chart.js
            Map<String, BigDecimal> categoriasMap = new HashMap<>();
            List<String> carteirasLabels = new ArrayList<>();
            List<BigDecimal> carteirasData = new ArrayList<>();

            for (Carteira c : carteiras) {
                BigDecimal patCarteira = c.getSaldoDisponivel();
                totalCaixa = totalCaixa.add(c.getSaldoDisponivel());
                carteirasLabels.add(c.getNome());

                for (Posicao p : c.getPosicoes()) {
                    if (p.getQuantidadeAtual() > 0) {
                        BigDecimal capitalAlocado = p.getPrecoMedio().multiply(new BigDecimal(p.getQuantidadeAtual()));
                        patCarteira = patCarteira.add(capitalAlocado);
                        totalInvestido = totalInvestido.add(capitalAlocado);

                        String cat = p.getAtivo().getCategoria();
                        categoriasMap.put(cat, categoriasMap.getOrDefault(cat, BigDecimal.ZERO).add(capitalAlocado));
                    }
                }
                carteirasData.add(patCarteira);
            }

            BigDecimal patrimonioTotal = totalCaixa.add(totalInvestido);
            BigDecimal percentualAlocado = BigDecimal.ZERO;

            if (patrimonioTotal.compareTo(BigDecimal.ZERO) > 0) {
                percentualAlocado = totalInvestido.divide(patrimonioTotal, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            }

            // Injeta os dados já processados no ecrã
            model.addAttribute("carteirasCount", carteiras.size());
            model.addAttribute("patrimonioTotal", patrimonioTotal);
            model.addAttribute("totalCaixa", totalCaixa);
            model.addAttribute("totalInvestido", totalInvestido);
            model.addAttribute("percentualAlocado", percentualAlocado);

            // Injeta as arrays limpas para os Gráficos
            model.addAttribute("catLabels", categoriasMap.keySet());
            model.addAttribute("catData", categoriasMap.values());
            model.addAttribute("cartLabels", carteirasLabels);
            model.addAttribute("cartData", carteirasData);
        }
        return "relatorios";
    }
}