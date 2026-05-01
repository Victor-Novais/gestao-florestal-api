package com.grupo6.gestao_florestal_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioEspecieResponseDTO {

    private UUID id;
    private UUID especieId;
    private String especieNome;
    private String especieNomeCientifico;
    private Integer quantidade;
    private BigDecimal dapMedio;
    private BigDecimal alturaMedia;
}