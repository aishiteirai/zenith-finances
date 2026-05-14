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

    // Este é um exemplo de rota protegida para onde o utilizador será enviado após entrar
    @GetMapping("/home")
    public String home() {
        return "home"; // Precisarás de criar um ficheiro home.html nos teus templates futuramente
    }
}