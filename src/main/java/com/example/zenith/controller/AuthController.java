package com.example.zenith.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        // Retorna o nome do ficheiro HTML que está dentro de src/main/resources/templates (sem a extensão .html)
        return "login";
    }
    @GetMapping("/")
    public String paginaInicial() {
        return "index"; // Retorna o index.html que acabámos de criar
    }
}