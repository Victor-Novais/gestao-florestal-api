package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.Bioma;

import java.math.BigDecimal;

public record CoberturaBiomaDTO(
        Bioma bioma,
        BigDecimal hectaresMonitorados,
        BigDecimal hectaresTotais,
        BigDecimal taxaCobertura
) {
}
