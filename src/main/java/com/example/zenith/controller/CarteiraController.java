package com.example.zenith.controller;

import com.example.zenith.service.CarteiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class CarteiraController {

    @Autowired
    private CarteiraService carteiraService;

    @PostMapping("/carteiras/nova")
    public String criarNovaCarteira(@RequestParam String nome,
                                    @RequestParam(required = false, defaultValue = "0") BigDecimal saldoInicial,
                                    @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails != null) {
            carteiraService.criarCarteira(userDetails.getUsername(), nome, saldoInicial);
        }

        return "redirect:/home";
    }
}