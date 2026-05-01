package com.grupo6.gestao_florestal_api.dto;

import jakarta.validation.constraints.*;
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
public class InventarioEspecieItemDTO {

    @NotNull(message = "ID da espécie vegetal é obrigatório")
    private UUID especieId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    private Integer quantidade;

    @NotNull(message = "DAP médio é obrigatório")
    @DecimalMin(value = "0.1", message = "DAP médio deve ser maior que 0")
    @DecimalMax(value = "500.0", message = "DAP médio não pode exceder 500 cm")
    private BigDecimal dapMedio;

    @NotNull(message = "Altura média é obrigatória")
    @DecimalMin(value = "0.1", message = "Altura média deve ser maior que 0")
    @DecimalMax(value = "100.0", message = "Altura média não pode exceder 100 metros")
    private BigDecimal alturaMedia;
}