package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.TipoOcorrencia;
import com.grupo6.gestao_florestal_api.domain.enums.UrgenciaOcorrencia;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OcorrenciaRequestDTO {

    @NotNull(message = "Tipo é obrigatório")
    private TipoOcorrencia tipo;

    @NotNull(message = "Área Florestal é obrigatória")
    private UUID areaFlorestalId;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotNull(message = "Urgência é obrigatória")
    private UrgenciaOcorrencia urgencia;

    private String descricao;
    private List<String> urlFotos;
}