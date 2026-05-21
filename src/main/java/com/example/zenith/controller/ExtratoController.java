package com.example.zenith.controller;

import com.example.zenith.model.Transacao;
import com.example.zenith.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ExtratoController {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @GetMapping("/extrato")
    public String exibirExtrato(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String email = userDetails.getUsername();

            // Busca o histórico completo do usuário
            List<Transacao> transacoes = transacaoRepository.findByCarteiraInvestidorEmailOrderByDataOperacaoDesc(email);

            model.addAttribute("transacoes", transacoes);
            model.addAttribute("username", email);
        }
        return "extrato";
    }
}