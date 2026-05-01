package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.EquipamentoInsumo;
import com.grupo6.gestao_florestal_api.domain.enums.CategoriaEquipamento;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public record PrevisaoReposicaoDTO(
        UUID id,
        String codigoPatrimonial,
        String descricao,
        CategoriaEquipamento categoria,
        String localizacaoAtual,
        LocalDate dataAquisicao,
        Integer vidaUtilEstimada,   // em meses
        LocalDate dataVencimento,   // dataAquisicao + vidaUtilEstimada meses
        Long diasRestantes          // dataVencimento - hoje (pode ser negativo se já vencido)
) {
    public static PrevisaoReposicaoDTO from(EquipamentoInsumo e) {
        LocalDate dataVencimento = e.getDataAquisicao().plusMonths(e.getVidaUtilEstimada());
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), dataVencimento);

        return new PrevisaoReposicaoDTO(
                e.getId(),
                e.getCodigoPatrimonial(),
                e.getDescricao(),
                e.getCategoria(),
                e.getLocalizacaoAtual(),
                e.getDataAquisicao(),
                e.getVidaUtilEstimada(),
                dataVencimento,
                diasRestantes
        );
    }
}
