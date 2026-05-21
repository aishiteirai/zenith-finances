package com.example.zenith.controller;

import com.example.zenith.model.Investidor;
import com.example.zenith.repository.InvestidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

    @Autowired
    private InvestidorRepository investidorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/perfil")
    public String exibirPerfil(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String email = userDetails.getUsername();
            Investidor investidor = investidorRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Investidor de rede não encontrado."));

            model.addAttribute("investidor", investidor);
            model.addAttribute("username", email);
        }
        return "perfil";
    }

    @PostMapping("/perfil/atualizar")
    public String atualizarDados(@RequestParam String nome,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            String email = userDetails.getUsername();
            Investidor investidor = investidorRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Investidor não encontrado."));

            investidor.setNome(nome);
            investidorRepository.save(investidor);

            redirectAttributes.addFlashAttribute("sucessoDados", "Identidade de rede atualizada com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erroDados", "Falha ao atualizar dados: " + e.getMessage());
        }
        return "redirect:/perfil";
    }

    @PostMapping("/perfil/senha")
    public String alterarSenha(@RequestParam String senhaAtual,
                               @RequestParam String novaSenha,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            String email = userDetails.getUsername();
            Investidor investidor = investidorRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Investidor não encontrado."));

            // Valida se a senha atual está correta antes de encriptar a nova
            if (!passwordEncoder.matches(senhaAtual, investidor.getSenhaHash())) {
                redirectAttributes.addFlashAttribute("erroSenha", "Protocolo Recusado: Senha atual inválida.");
                return "redirect:/perfil";
            }

            if (novaSenha.length() < 12) {
                redirectAttributes.addFlashAttribute("erroSenha", "Protocolo Recusado: Nova chave deve conter no mínimo 12 caracteres.");
                return "redirect:/perfil";
            }

            investidor.setSenhaHash(passwordEncoder.encode(novaSenha));
            investidorRepository.save(investidor);

            redirectAttributes.addFlashAttribute("sucessoSenha", "Chave de acesso criptografada e atualizada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erroSenha", "Erro interno no processamento: " + e.getMessage());
        }
        return "redirect:/perfil";
    }
}