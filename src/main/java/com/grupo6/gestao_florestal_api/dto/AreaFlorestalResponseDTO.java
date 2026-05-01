package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.Bioma;
import com.grupo6.gestao_florestal_api.domain.enums.StatusArea;
import com.grupo6.gestao_florestal_api.domain.enums.TipoFloresta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AreaFlorestalResponseDTO(UUID id,
                                       String identificadorUnico,
                                       String nome,
                                       BigDecimal latitude,
                                       BigDecimal longitude,
                                       String municipio,
                                       String estado,
                                       BigDecimal hectares,
                                       TipoFloresta tipoFloresta,
                                       Bioma bioma,
                                       StatusArea status,
                                       Boolean ativo,
                                       LocalDateTime CriadoEm,
                                       LocalDateTime dataInativacao
) {}
