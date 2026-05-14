package com.example.zenith.controller;

import com.example.zenith.model.Investidor;
import com.example.zenith.repository.InvestidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistroController {

    @Autowired
    private InvestidorRepository investidorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Mostra o ecrã de registo
    @GetMapping("/register")
    public String exibirFormularioRegistro() {
        return "register";
    }

    // Recebe os dados quando o utilizador clica em "INITIALIZE"
    @PostMapping("/register")
    public String processarRegistro(@RequestParam String nome,
                                    @RequestParam String email,
                                    @RequestParam String senha,
                                    Model model) {

        // 1. Verifica se o e-mail já está em uso
        if (investidorRepository.findByEmail(email).isPresent()) {
            model.addAttribute("erro", true); // Mostra a mensagem de erro no HTML
            return "register";
        }

        // 2. Cria o novo investidor
        Investidor novoInvestidor = new Investidor();
        novoInvestidor.setNome(nome);
        novoInvestidor.setEmail(email);

        // 3. Criptografa a senha antes de salvar (Muito Importante!)
        novoInvestidor.setSenhaHash(passwordEncoder.encode(senha));

        // 4. Guarda na base de dados H2
        investidorRepository.save(novoInvestidor);

        // 5. Redireciona para a página de login com um aviso de sucesso
        return "redirect:/login?registered";
    }
}