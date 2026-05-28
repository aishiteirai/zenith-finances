package com.example.zenith.service;

import com.example.zenith.dto.TransacaoRequestDTO;
import com.example.zenith.model.*;
import com.example.zenith.repository.*;
import com.example.zenith.exception.SaldoInsuficienteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class TransacaoService {

    @Autowired private CarteiraRepository carteiraRepository;
    @Autowired private AtivoRepository ativoRepository;
    @Autowired private PosicaoRepository posicaoRepository;
    @Autowired private TransacaoRepository transacaoRepository;

    @Transactional
    public void executarTransacao(TransacaoRequestDTO dto, String emailUsuario) {
        Carteira carteira = carteiraRepository.findByIdAndInvestidorEmail(dto.getCarteiraId(), emailUsuario)
                .orElseThrow(() -> new SecurityException("Acesso negado."));

        Ativo ativo = ativoRepository.findById(dto.getAtivoId())
                .orElseThrow(() -> new RuntimeException("Ativo não encontrado"));

        // Define o Preço Real de Mercado (Mark-to-Market)
        BigDecimal precoExecucao = (ativo.getPrecoAtual() != null && ativo.getPrecoAtual().compareTo(BigDecimal.ZERO) > 0)
                ? ativo.getPrecoAtual()
                : dto.getPrecoUnitario();

        BigDecimal valorTotal = precoExecucao.multiply(new BigDecimal(dto.getQuantidade()));

        // Busca a posição existente ou cria uma NOVA de forma segura (sem construtor com parâmetros)
        Posicao posicao = posicaoRepository.findByCarteiraIdAndAtivoId(carteira.getId(), ativo.getId())
                .orElseGet(() -> {
                    Posicao nova = new Posicao();
                    nova.setCarteira(carteira);
                    nova.setAtivo(ativo);
                    nova.setQuantidadeAtual(0);
                    nova.setPrecoMedio(BigDecimal.ZERO);
                    return nova;
                });

        if (dto.getTipo() == TipoTransacao.COMPRA) {
            if (carteira.getSaldoDisponivel().compareTo(valorTotal) < 0) {
                throw new SaldoInsuficienteException("Saldo em caixa insuficiente para esta compra.");
            }
            carteira.setSaldoDisponivel(carteira.getSaldoDisponivel().subtract(valorTotal));

            // Matemática segura in-line (substitui o antigo método atualizarPosicao que gerava erros)
            int novaQtd = posicao.getQuantidadeAtual() + dto.getQuantidade();
            BigDecimal totalAtual = posicao.getPrecoMedio().multiply(new BigDecimal(posicao.getQuantidadeAtual()));
            BigDecimal novoPrecoMedio = totalAtual.add(valorTotal).divide(new BigDecimal(novaQtd), 6, RoundingMode.HALF_UP);

            posicao.setQuantidadeAtual(novaQtd);
            posicao.setPrecoMedio(novoPrecoMedio);
            posicaoRepository.save(posicao);

        } else if (dto.getTipo() == TipoTransacao.VENDA) {
            posicao = posicaoRepository.findByCarteiraIdAndAtivoId(carteira.getId(), ativo.getId())
                    .orElseThrow(() -> new RuntimeException("Você não possui este ativo nesta carteira."));

            if (posicao.getQuantidadeAtual() < dto.getQuantidade()) {
                throw new RuntimeException("Quantidade insuficiente. Você possui apenas " + posicao.getQuantidadeAtual() + " unidades.");
            }

            // O Caixa recebe o valor TOTAL VALORIZADO (Ex: 0.10 BTC + 10% = 0.11)
            carteira.setSaldoDisponivel(carteira.getSaldoDisponivel().add(valorTotal));
            posicao.setQuantidadeAtual(posicao.getQuantidadeAtual() - dto.getQuantidade());
            posicaoRepository.save(posicao);
        }

        // Instancia a Transacao de forma segura usando Setters
        Transacao transacao = new Transacao();
        transacao.setCarteira(carteira);
        transacao.setAtivo(ativo);
        transacao.setTipo(dto.getTipo());
        transacao.setQuantidade(dto.getQuantidade());
        transacao.setPrecoUnitario(precoExecucao);
        transacao.setDataOperacao(LocalDateTime.now());

        transacaoRepository.save(transacao);
        carteiraRepository.save(carteira);
    }
}