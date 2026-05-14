package com.example.zenith.controller;

import com.example.zenith.dto.RegistroDTO;
import com.example.zenith.service.InvestidorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistroController {

    @Autowired
    private InvestidorService investidorService;

    @GetMapping("/register")
    public String exibirFormularioRegistro(Model model) {
        model.addAttribute("registroDTO", new RegistroDTO());
        return "register";
    }

    @PostMapping("/register")
    public String processarRegistro(@Valid @ModelAttribute("registroDTO") RegistroDTO registroDTO,
                                    BindingResult result,
                                    Model model) {
        // Se houver erros de validação (ex: email inválido, senha curta), retorna à tela
        if (result.hasErrors()) {
            return "register";
        }

        boolean sucesso = investidorService.registrarNovoInvestidor(registroDTO);

        if (!sucesso) {
            model.addAttribute("erro", true);
            return "register";
        }

        return "redirect:/login?registered";
    }
}