package com.grupo6.gestao_florestal_api.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record AcumuladoMensalDTO(
        Integer mes,
        Integer ano,
        Integer totalMudasMes,
        Integer metaMes,               // null se meta não definida
        BigDecimal percentualAtingido  // null se meta não definida
) {
    public static AcumuladoMensalDTO of(int mes, int ano, int total, Integer meta) {
        BigDecimal percentual = null;
        if (meta != null && meta > 0) {
            percentual = BigDecimal.valueOf(total)
                    .divide(BigDecimal.valueOf(meta), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return new AcumuladoMensalDTO(mes, ano, total, meta, percentual);
    }
}
