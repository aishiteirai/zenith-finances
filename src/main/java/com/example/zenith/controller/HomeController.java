package com.example.zenith.controller;

import com.example.zenith.model.Carteira;
import com.example.zenith.model.Investidor;
import com.example.zenith.service.CarteiraService;
import com.example.zenith.repository.AtivoRepository;
import com.example.zenith.repository.InvestidorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CarteiraService carteiraService;

    // 1. INJETANDO OS REPOSITÓRIOS NO CONTROLLER
    @Autowired
    private InvestidorRepository investidorRepository;

    @Autowired
    private AtivoRepository ativoRepository;

    @GetMapping("/")
    public String paginaInicial() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        // 2. USANDO A VARIÁVEL INSTANCIADA (letra minúscula)
        Investidor investidor = investidorRepository.findByEmail(email).get();
        List<Carteira> carteiras = carteiraService.listarCarteiras(email);

        // Cálculos de Patrimônio
        BigDecimal totalInvestido = carteiras.stream()
                .flatMap(c -> c.getPosicoes().stream())
                .map(p -> p.getPrecoMedio().multiply(new BigDecimal(p.getQuantidadeAtual())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("investidor", investidor);
        model.addAttribute("saldoInercia", investidor.getSaldoGlobal());
        model.addAttribute("saldoInvestido", totalInvestido);
        model.addAttribute("carteiras", carteiras);

        // 3. USANDO A VARIÁVEL INSTANCIADA (letra minúscula)
        model.addAttribute("rankingAcoes", ativoRepository.findByCategoria("ACAO"));
        model.addAttribute("rankingCripto", ativoRepository.findByCategoria("CRIPTO"));

        return "home";
    }
}