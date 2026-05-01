package com.grupo6.gestao_florestal_api.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record MetaPlantioRequestDTO(

        @NotNull(message = "Área florestal é obrigatória")
        UUID areaFlorestalId,

        @NotNull(message = "Mês é obrigatório")
        @Min(value = 1, message = "Mês deve ser entre 1 e 12")
        @Max(value = 12, message = "Mês deve ser entre 1 e 12")
        Integer mes,

        @NotNull(message = "Ano é obrigatório")
        @Min(value = 2000, message = "Ano deve ser 2000 ou posterior")
        Integer ano,

        @NotNull(message = "Quantidade meta é obrigatória")
        @Min(value = 1, message = "Quantidade meta deve ser maior que zero")
        Integer quantidadeMeta
) {}
