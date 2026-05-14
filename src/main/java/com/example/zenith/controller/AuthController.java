package com.example.zenith.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String paginaInicial() {
        return "index";
    }

    // O novo mapeamento da Home
    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Passamos o e-mail do usuário logado para a tela
        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());
        }
        return "home"; // Irá procurar por home.html em templates
    }
}