package com.grupo6.gestao_florestal_api.dto;

import java.util.List;

public record HistoricoParcelaResponseDTO(
        List<HistoricoParcelaItemDTO> itens
) {
}
