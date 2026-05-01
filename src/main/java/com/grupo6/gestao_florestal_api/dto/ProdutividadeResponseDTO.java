package com.grupo6.gestao_florestal_api.dto;

import java.time.LocalDate;
import java.util.List;

public record ProdutividadeResponseDTO(
        LocalDate dataInicio,
        LocalDate dataFim,
        List<TotalMudasAreaDTO> totalMudasPorArea,
        List<CoberturaBiomaDTO> coberturaPorBioma,
        List<OcorrenciaTipoUrgenciaDTO> ocorrenciasPorTipoUrgencia,
        IndiceSaudeFlorestalDTO indiceSaudeFlorestal
) {
}
