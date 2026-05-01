package com.grupo6.gestao_florestal_api.dto;

import java.time.LocalDate;
import java.util.List;

public record OcorrenciaConsolidadoResponseDTO(
        LocalDate dataInicio,
        LocalDate dataFim,
        List<OcorrenciaConsolidadoItemDTO> itens
) {
}
