package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.Porte;
import com.grupo6.gestao_florestal_api.domain.enums.StatusConservacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record FichaTecnicaEspecieDTO(UUID id,
                                     String nomeCientifico,
                                     String nomePopular,
                                     String familiaBotanica,
                                     Porte porte,
                                     StatusConservacao statusConservacao,
                                     Integer cicloVidaAnos,
                                     String exigenciasClimaticas,
                                     String exigenciasSolo,
                                     Boolean nativa,
                                     LocalDateTime dataIdentificacao,
                                     Long totalAreas) {
}
