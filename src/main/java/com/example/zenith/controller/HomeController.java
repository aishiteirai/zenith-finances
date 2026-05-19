package com.example.zenith.controller;

import com.example.zenith.service.CarteiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private CarteiraService carteiraService;

    @GetMapping("/")
    public String paginaInicial() {
        return "index";
    }

    @GetMapping("/home")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String email = userDetails.getUsername();
            model.addAttribute("username", email);
            model.addAttribute("carteiras", carteiraService.listarCarteiras(email));
        }
        return "home";
    }
}