package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.CategoriaEquipamento;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EquipamentoInsumoRequestDTO(

        @NotBlank(message = "Código patrimonial é obrigatório")
        String codigoPatrimonial,

        @NotBlank(message = "Descrição é obrigatória")
        String descricao,

        @NotNull(message = "Categoria é obrigatória")
        CategoriaEquipamento categoria,

        @NotNull(message = "Quantidade é obrigatória")
        @DecimalMin(value = "0.0", message = "Quantidade não pode ser negativa")
        BigDecimal quantidade,

        @NotBlank(message = "Unidade de medida é obrigatória")
        String unidadeMedida,

        String localizacaoAtual,

        @NotNull(message = "Data de aquisição é obrigatória")
        @PastOrPresent(message = "Data de aquisição não pode ser futura")
        LocalDate dataAquisicao,

        @NotNull(message = "Vida útil estimada é obrigatória")
        @Min(value = 1, message = "Vida útil estimada deve ser de no mínimo 1 mês")
        Integer vidaUtilEstimada,

        @NotNull(message = "Estoque mínimo é obrigatório")
        @DecimalMin(value = "0.0", message = "Estoque mínimo não pode ser negativo")
        BigDecimal estoqueMinimo,

        // UUID do colaborador responsável pela guarda (opcional)
        UUID responsavelId
) {}
