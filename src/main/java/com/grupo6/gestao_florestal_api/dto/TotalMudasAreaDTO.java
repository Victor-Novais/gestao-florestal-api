package com.grupo6.gestao_florestal_api.dto;

import java.util.UUID;

public record TotalMudasAreaDTO(
        UUID areaId,
        String areaNome,
        Integer totalMudas
) {
}
