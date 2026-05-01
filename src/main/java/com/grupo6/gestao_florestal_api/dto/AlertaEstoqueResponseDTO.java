package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.EquipamentoInsumo;
import com.grupo6.gestao_florestal_api.domain.enums.CategoriaEquipamento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public record AlertaEstoqueResponseDTO(
        UUID id,
        String codigoPatrimonial,
        String descricao,
        CategoriaEquipamento categoria,
        BigDecimal quantidade,
        BigDecimal estoqueMinimo,
        String unidadeMedida,
        String localizacaoAtual,
        // (quantidade / estoqueMinimo) * 100, ou 0.0 se estoqueMinimo = 0
        BigDecimal percentualRestante
) {
    public static AlertaEstoqueResponseDTO from(EquipamentoInsumo e) {
        BigDecimal percentual;
        if (e.getEstoqueMinimo().compareTo(BigDecimal.ZERO) == 0) {
            percentual = BigDecimal.ZERO;
        } else {
            percentual = e.getQuantidade()
                    .divide(e.getEstoqueMinimo(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return new AlertaEstoqueResponseDTO(
                e.getId(),
                e.getCodigoPatrimonial(),
                e.getDescricao(),
                e.getCategoria(),
                e.getQuantidade(),
                e.getEstoqueMinimo(),
                e.getUnidadeMedida(),
                e.getLocalizacaoAtual(),
                percentual
        );
    }
}
