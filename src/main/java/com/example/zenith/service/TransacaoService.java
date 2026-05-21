package com.example.zenith.service;

import com.example.zenith.dto.TransacaoRequestDTO;
import com.example.zenith.exception.SaldoInsuficienteException;
import com.example.zenith.model.*;
import com.example.zenith.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransacaoService {

    @Autowired private CarteiraRepository carteiraRepository;
    @Autowired private AtivoRepository ativoRepository;
    @Autowired private PosicaoRepository posicaoRepository;
    @Autowired private TransacaoRepository transacaoRepository;

    @Transactional
    public Transacao registrarNovaTransacao(TransacaoRequestDTO dto) {
        // 1. Busca da Carteira e do Ativo
        Carteira carteira = carteiraRepository.findById(dto.getCarteiraId())
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada."));

        Ativo ativo = ativoRepository.findById(dto.getAtivoId())
                .orElseThrow(() -> new RuntimeException("Ativo não encontrado."));

        // CORREÇÃO: Usando o 'dto' em vez de 'transacao' (que ainda não foi instanciada)
        BigDecimal valorTotal = dto.getPrecoUnitario().multiply(new BigDecimal(dto.getQuantidade()));

        if (ativo.getValorMinimo() != null && valorTotal.compareTo(ativo.getValorMinimo()) < 0) {
            throw new RuntimeException("Ordem Rejeitada: O valor total da operação (R$ " + valorTotal
                    + ") é inferior ao aporte mínimo estipulado para o ativo " + ativo.getTicker()
                    + " (Mínimo: R$ " + ativo.getValorMinimo() + ").");
        }

        // 2. Validação de Saldo (se for compra)
        if (dto.getTipo() == TipoTransacao.COMPRA) {
            if (!carteira.verificarSaldoSuficiente(valorTotal)) {
                // Lança exceção que será capturada pelo Controller
                throw new SaldoInsuficienteException("Saldo Insuficiente na carteira.");
            }
            // 3. Deduzir Saldo
            carteira.deduzirSaldo(valorTotal);
        } else if (dto.getTipo() == TipoTransacao.VENDA) {
            // Se for venda, credita o saldo
            carteira.adicionarSaldo(valorTotal);
        }

        // 4. Busca ou criação da Posição
        Posicao posicao = posicaoRepository.findByCarteiraIdAndAtivoId(carteira.getId(), ativo.getId())
                .orElseGet(() -> {
                    Posicao nova = new Posicao();
                    nova.setCarteira(carteira);
                    nova.setAtivo(ativo);
                    return nova;
                });

        // 5. Atualizações em Memória
        posicao.atualizarPosicao(dto.getQuantidade(), dto.getPrecoUnitario(), dto.getTipo());

        // Cria o registro histórico da transação
        Transacao transacao = new Transacao();
        transacao.setCarteira(carteira);
        transacao.setAtivo(ativo);
        transacao.setTipo(dto.getTipo());
        transacao.setQuantidade(dto.getQuantidade());
        transacao.setPrecoUnitario(dto.getPrecoUnitario());

        // 6. Persistência
        carteiraRepository.save(carteira);
        posicaoRepository.save(posicao);
        transacaoRepository.save(transacao);

        // 7. Retorno de Sucesso
        return transacao;
    }
}