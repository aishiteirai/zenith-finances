package com.example.zenith.controller;

import com.example.zenith.model.*;
import com.example.zenith.repository.*;
import com.example.zenith.service.CarteiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

@Controller
public class RelatorioController {

    @Autowired private AtivoRepository ativoRepository;
    @Autowired private CarteiraService carteiraService;
    @Autowired private InvestidorRepository investidorRepository;
    @Autowired private TransacaoRepository transacaoRepository;

    @GetMapping("/relatorios")
    public String exibirAnalytics(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String email = userDetails.getUsername();
            Investidor investidor = investidorRepository.findByEmail(email).orElse(null);
            model.addAttribute("investidor", investidor);

            List<Carteira> carteiras = carteiraService.listarCarteiras(email);

            BigDecimal totalCaixa = BigDecimal.ZERO;
            BigDecimal totalInvestidoAtual = BigDecimal.ZERO;

            Map<String, BigDecimal> categoriasMap = new HashMap<>();
            List<String> carteirasLabels = new ArrayList<>();
            List<BigDecimal> carteirasData = new ArrayList<>();

            for (Carteira c : carteiras) {
                BigDecimal patCarteira = c.getSaldoDisponivel();
                totalCaixa = totalCaixa.add(c.getSaldoDisponivel());
                carteirasLabels.add(c.getNome());

                for (Posicao p : c.getPosicoes()) {
                    if (p.getQuantidadeAtual() > 0) {
                        BigDecimal capitalAlocado = p.getPrecoMedio().multiply(new BigDecimal(p.getQuantidadeAtual()));
                        patCarteira = patCarteira.add(capitalAlocado);
                        totalInvestidoAtual = totalInvestidoAtual.add(capitalAlocado);

                        String cat = p.getAtivo().getCategoria();
                        categoriasMap.put(cat, categoriasMap.getOrDefault(cat, BigDecimal.ZERO).add(capitalAlocado));
                    }
                }
                carteirasData.add(patCarteira);
            }

            BigDecimal patrimonioTotal = totalCaixa.add(totalInvestidoAtual);
            BigDecimal percentualAlocado = BigDecimal.ZERO;
            if (patrimonioTotal.compareTo(BigDecimal.ZERO) > 0) {
                percentualAlocado = totalInvestidoAtual.divide(patrimonioTotal, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            }

            // ==========================================
            // MOTOR DE CÁLCULO HISTÓRICO (ÚLTIMOS 6 MESES)
            // ==========================================
            List<Transacao> todasTransacoes = transacaoRepository.findAll().stream()
                    .filter(t -> t.getCarteira().getInvestidor().getEmail().equals(email))
                    .toList();

            List<String> labelsMeses = new ArrayList<>();
            List<BigDecimal> historicoAum = new ArrayList<>();
            YearMonth mesAtual = YearMonth.now();

            for (int i = 5; i >= 0; i--) {
                YearMonth ym = mesAtual.minusMonths(i);
                String nomeMes = ym.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));
                labelsMeses.add(nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1));

                LocalDateTime fimDoMes = ym.atEndOfMonth().atTime(23, 59, 59);

                BigDecimal investidoAteOMes = BigDecimal.ZERO;
                for(Transacao t : todasTransacoes) {
                    if(!t.getDataOperacao().isAfter(fimDoMes)) {
                        BigDecimal valorTx = t.getPrecoUnitario().multiply(new BigDecimal(t.getQuantidade()));
                        if(t.getTipo() == TipoTransacao.COMPRA) {
                            investidoAteOMes = investidoAteOMes.add(valorTx);
                        } else if (t.getTipo() == TipoTransacao.VENDA) {
                            investidoAteOMes = investidoAteOMes.subtract(valorTx);
                        }
                    }
                }

                historicoAum.add(totalCaixa.add(investidoAteOMes));
            }

            // ==========================================
            // NOVO: RANKING DE RENTABILIDADE E POSIÇÕES
            // ==========================================

            // 1. Extrai todas as posições do usuário para a tabela de Performance da Carteira
            List<Posicao> todasPosicoes = new ArrayList<>();
            for (Carteira c : carteiras) {
                todasPosicoes.addAll(c.getPosicoes());
            }
            // Ordena da posição com maior dinheiro alocado para a menor
            todasPosicoes.sort((p1, p2) -> {
                BigDecimal valorP2 = p2.getPrecoMedio().multiply(new BigDecimal(p2.getQuantidadeAtual()));
                BigDecimal valorP1 = p1.getPrecoMedio().multiply(new BigDecimal(p1.getQuantidadeAtual()));
                return valorP2.compareTo(valorP1);
            });

            // 2. Busca os TOP 5 Ativos do Catálogo com a MAIOR Taxa de Rendimento

            List<Ativo> topAtivosRentabilidade = ativoRepository.findAll().stream()
                    .filter(Ativo::isVisivel) // Apenas ativos disponíveis
                    .sorted((a1, a2) -> a2.getTaxaRendimentoEstimada().compareTo(a1.getTaxaRendimentoEstimada()))
                    .limit(5)
                    .toList();

            // Adiciona as listas ranqueadas na View para a tabela iterar
            model.addAttribute("minhasPosicoes", todasPosicoes);
            model.addAttribute("topAtivos", topAtivosRentabilidade);
            model.addAttribute("carteirasCount", carteiras.size());
            model.addAttribute("patrimonioTotal", patrimonioTotal);
            model.addAttribute("totalCaixa", totalCaixa);
            model.addAttribute("totalInvestido", totalInvestidoAtual);
            model.addAttribute("percentualAlocado", percentualAlocado);

            model.addAttribute("catLabels", categoriasMap.keySet());
            model.addAttribute("catData", categoriasMap.values());
            model.addAttribute("cartLabels", carteirasLabels);
            model.addAttribute("cartData", carteirasData);

            model.addAttribute("labelsMeses", labelsMeses);
            model.addAttribute("historicoAum", historicoAum);
        }
        return "relatorios";
    }
}