package com.example.zenith.controller;

import com.example.zenith.model.Carteira;
import com.example.zenith.service.CarteiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RelatorioController {

    @Autowired
    private CarteiraService carteiraService;

    @GetMapping("/relatorios")
    public String exibirAnalytics(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String email = userDetails.getUsername();

            // Busca todas as carteiras e suas posições associadas ao usuário
            List<Carteira> carteiras = carteiraService.listarCarteiras(email);

            model.addAttribute("carteiras", carteiras);
            model.addAttribute("username", email);
        }
        return "relatorios";
    }
}