package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.EstadoGeral;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record HistoricoParcelaItemDTO(
        UUID inventarioId,
        LocalDate dataVistoria,
        String parcela,
        UUID areaId,
        EstadoGeral estadoGeral,
        String mudancaEstadoGeral,
        BigDecimal dapMedioAtual,
        BigDecimal variacaoDap,
        String tendencia,
        List<VariacaoEspecieDTO> especies
) {
}
