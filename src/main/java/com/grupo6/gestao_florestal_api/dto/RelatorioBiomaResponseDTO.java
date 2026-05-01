package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.Bioma;
import com.grupo6.gestao_florestal_api.domain.enums.TipoFloresta;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioBiomaResponseDTO(Bioma bioma,
                                        Long totalAreas,
                                        BigDecimal totalHectares,
                                        List<RelatorioPorTipoDTO> porTipo) {
    public record RelatorioPorTipoDTO(
            TipoFloresta tipoFloresta,
            Long totalAreas,
            BigDecimal totalHectares
    ) {}
}

