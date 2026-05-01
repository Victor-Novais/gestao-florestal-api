package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.MetodoPlantio;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PlantioRequestDTO(

        @NotNull(message = "Data e hora são obrigatórias")
        LocalDateTime dataHora,

        @NotNull(message = "Área florestal é obrigatória")
        UUID areaFlorestalId,

        @NotNull(message = "Espécie vegetal é obrigatória")
        UUID especieVegetalId,

        @NotNull(message = "Colaborador é obrigatório")
        UUID colaboradorId,

        @NotNull(message = "Quantidade de mudas é obrigatória")
        @Min(value = 1, message = "Quantidade de mudas deve ser maior que zero")
        Integer quantidadeMudas,

        BigDecimal latitudeTalhao,
        BigDecimal longitudeTalhao,
        BigDecimal temperatura,
        BigDecimal umidade,
        boolean chuva,

        @NotNull(message = "Método de plantio é obrigatório")
        MetodoPlantio metodoPlantio,

        String observacoes
) {}
