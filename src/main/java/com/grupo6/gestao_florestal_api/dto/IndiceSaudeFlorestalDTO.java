package com.grupo6.gestao_florestal_api.dto;

import java.math.BigDecimal;

public record IndiceSaudeFlorestalDTO(
        BigDecimal mediaScore,
        Integer totalInventarios
) {
}
