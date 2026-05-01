package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.EquipamentoInsumo;
import com.grupo6.gestao_florestal_api.domain.enums.CategoriaEquipamento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

public record EquipamentoInsumoResponseDTO(
        UUID id,
        String codigoPatrimonial,
        String descricao,
        CategoriaEquipamento categoria,
        BigDecimal quantidade,
        String unidadeMedida,
        String localizacaoAtual,
        LocalDate dataAquisicao,
        Integer vidaUtilEstimada,
        BigDecimal estoqueMinimo,
        boolean ativo,
        UUID responsavelId,
        String responsavelNome,

        // true quando quantidade <= estoqueMinimo
        boolean alertaEstoqueBaixo
) {
    public static EquipamentoInsumoResponseDTO from(EquipamentoInsumo e) {
        boolean alerta = e.getQuantidade().compareTo(e.getEstoqueMinimo()) <= 0;

        return new EquipamentoInsumoResponseDTO(
                e.getId(),
                e.getCodigoPatrimonial(),
                e.getDescricao(),
                e.getCategoria(),
                e.getQuantidade(),
                e.getUnidadeMedida(),
                e.getLocalizacaoAtual(),
                e.getDataAquisicao(),
                e.getVidaUtilEstimada(),
                e.getEstoqueMinimo(),
                e.isAtivo(),
                e.getResponsavel() != null ? e.getResponsavel().getId() : null,
                e.getResponsavel() != null ? e.getResponsavel().getNomeCompleto() : null,
                alerta
        );
    }
}
