package com.example.zenith.controller;

import com.example.zenith.model.Ativo;
import com.example.zenith.model.Investidor;
import com.example.zenith.model.Transacao;
import com.example.zenith.repository.AtivoRepository;
import com.example.zenith.repository.InvestidorRepository;
import com.example.zenith.repository.TransacaoRepository;
import com.example.zenith.repository.CarteiraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private InvestidorRepository investidorRepository;
    @Autowired private AtivoRepository ativoRepository;
    @Autowired private TransacaoRepository transacaoRepository;
    @Autowired private CarteiraRepository carteiraRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 1. Telemetria Quantitativa da Rede
        long totalUsuarios = investidorRepository.count();
        long totalAtivos = ativoRepository.count();
        long totalTransacoes = transacaoRepository.count();

        // 2. Cálculo do AUM Global (Assets Under Management)
        // Soma o saldo em inércia de todas as contas do sistema
        BigDecimal totalSaldosInercia = investidorRepository.findAll().stream()
                .map(Investidor::getSaldoGlobal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Soma o capital alocado em todas as carteiras da plataforma
        BigDecimal totalCaixaCarteiras = carteiraRepository.findAll().stream()
                .map(c -> c.getSaldoDisponivel())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal aumGlobal = totalSaldosInercia.add(totalCaixaCarteiras);

        // Envia as métricas consolidadas para a interface
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalAtivos", totalAtivos);
        model.addAttribute("totalTransacoes", totalTransacoes);
        model.addAttribute("aumGlobal", aumGlobal);

        return "admin/dashboard";
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Investidor> usuarios = investidorRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios";
    }

    @GetMapping("/ativos")
    public String listarAtivos(Model model) {
        List<Ativo> ativos = ativoRepository.findAll();
        model.addAttribute("ativos", ativos);
        model.addAttribute("novoAtivo", new Ativo()); // Molde para o formulário de cadastro
        return "admin/ativos";
    }

    @PostMapping("/ativos/novo")
    public String cadastrarAtivo(@ModelAttribute Ativo ativo) {
        // Garante a persistência do novo ativo no catálogo global
        ativoRepository.save(ativo);
        return "redirect:/admin/ativos";
    }

    @GetMapping("/transacoes")
    public String auditoriaTransacoes(Model model) {
        // Busca o ledger de auditoria total, ordenando ou listando de forma linear
        List<Transacao> transacoes = transacaoRepository.findAll();
        model.addAttribute("transacoes", transacoes);
        return "admin/transacoes";
    }

    @PostMapping("/usuarios/{id}/toggle-block")
    public String alternarBloqueioUsuario(@org.springframework.web.bind.annotation.PathVariable Long id) {
        Investidor investidor = investidorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Node não encontrado."));
        // Se for admin, não permite bloquear a si mesmo para evitar perda de acesso
        if (!investidor.getRole().equals("ROLE_ADMIN")) {
            investidor.setBloqueado(!investidor.isBloqueado());
            investidorRepository.save(investidor);
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/ativos/{id}/toggle-visibility")
    public String alternarVisibilidadeAtivo(@org.springframework.web.bind.annotation.PathVariable Long id) {
        Ativo ativo = ativoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ativo não localizado."));
        ativo.setVisivel(!ativo.isVisivel());
        ativoRepository.save(ativo);
        return "redirect:/admin/ativos";
    }
}