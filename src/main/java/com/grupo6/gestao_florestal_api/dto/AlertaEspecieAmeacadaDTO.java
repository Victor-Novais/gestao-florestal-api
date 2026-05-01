package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.Porte;
import com.grupo6.gestao_florestal_api.domain.enums.StatusConservacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlertaEspecieAmeacadaDTO(UUID id,
                                       String nomeCientifico,
                                       String nomePopular,
                                       Porte porte,
                                       StatusConservacao statusConservacao,
                                       LocalDateTime dataIdentificacao,
                                       Long totalAreas) {
}
