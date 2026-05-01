package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.TipoOcorrencia;

import java.math.BigDecimal;

public record OcorrenciaTipoUrgenciaDTO(
        TipoOcorrencia tipo,
        Integer baixo,
        Integer medio,
        Integer alto,
        Integer critico,
        Integer total,
        BigDecimal percentualCriticas
) {
}
