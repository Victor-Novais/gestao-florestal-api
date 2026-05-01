package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.EquipamentoInsumo;
import com.grupo6.gestao_florestal_api.domain.enums.CategoriaEquipamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InventarioEquipamentoDTO(
        UUID id,
        String codigoPatrimonial,
        String descricao,
        CategoriaEquipamento categoria,
        BigDecimal quantidade,
        BigDecimal estoqueMinimo,
        String unidadeMedida,
        String localizacaoAtual,
        LocalDate dataAquisicao,
        Integer vidaUtilEstimada,
        UUID responsavelId,
        String responsavelNome,
        StatusEstoque statusEstoque
) {
    public enum StatusEstoque {
        OK,      // quantidade > estoqueMinimo
        BAIXO,   // 0 < quantidade <= estoqueMinimo
        CRITICO  // quantidade = 0
    }

    public static InventarioEquipamentoDTO from(EquipamentoInsumo e) {
        StatusEstoque status;
        if (e.getQuantidade().compareTo(BigDecimal.ZERO) == 0) {
            status = StatusEstoque.CRITICO;
        } else if (e.getQuantidade().compareTo(e.getEstoqueMinimo()) <= 0) {
            status = StatusEstoque.BAIXO;
        } else {
            status = StatusEstoque.OK;
        }

        return new InventarioEquipamentoDTO(
                e.getId(),
                e.getCodigoPatrimonial(),
                e.getDescricao(),
                e.getCategoria(),
                e.getQuantidade(),
                e.getEstoqueMinimo(),
                e.getUnidadeMedida(),
                e.getLocalizacaoAtual(),
                e.getDataAquisicao(),
                e.getVidaUtilEstimada(),
                e.getResponsavel() != null ? e.getResponsavel().getId() : null,
                e.getResponsavel() != null ? e.getResponsavel().getNomeCompleto() : null,
                status
        );
    }
}
