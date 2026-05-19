package com.example.zenith.dto;

import com.example.zenith.model.TipoTransacao;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransacaoRequestDTO {
    private Long carteiraId;
    private Long ativoId;
    private int quantidade;
    private BigDecimal precoUnitario;
    private TipoTransacao tipo; // COMPRA ou VENDA
}