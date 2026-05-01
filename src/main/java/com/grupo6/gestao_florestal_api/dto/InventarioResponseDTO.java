package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.EstadoGeral;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioResponseDTO {

    private UUID id;
    private String numeroParcela;
    private UUID areaFlorestalId;
    private String areaFlorestalNome;
    private LocalDate dataVistoria;
    private Boolean presencaPragas;
    private String descricaoPragas;
    private EstadoGeral estadoGeral;
    private UUID colaboradorId;
    private String colaboradorNome;
    private LocalDateTime criadoEm;
    private List<InventarioEspecieResponseDTO> especies;
}