package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.TipoOcorrencia;
import com.grupo6.gestao_florestal_api.domain.enums.UrgenciaOcorrencia;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OcorrenciaResponseDTO {
    private UUID id;
    private String numeroProtocolo;
    private TipoOcorrencia tipo;
    private UUID areaFlorestalId;
    private String areaFlorestalNome;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private UrgenciaOcorrencia urgencia;
    private String descricao;
    private LocalDateTime dataRegistro;
    private UUID colaboradorId;
    private String colaboradorNome;
    private List<String> fotos;
    private Boolean alertaEmitido;
}