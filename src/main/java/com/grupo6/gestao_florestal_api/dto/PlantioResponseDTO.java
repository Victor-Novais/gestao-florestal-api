package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.RegistroPlantio;
import com.grupo6.gestao_florestal_api.domain.enums.MetodoPlantio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PlantioResponseDTO(
        UUID id,
        String numProtocolo,
        LocalDateTime dataHora,
        UUID areaFlorestalId,
        String areaFlorestalNome,
        UUID especieVegetalId,
        String especieVegetalNome,
        UUID colaboradorId,
        String colaboradorNome,
        Integer quantidadeMudas,
        BigDecimal latitudeTalhao,
        BigDecimal longitudeTalhao,
        BigDecimal temperatura,
        BigDecimal umidade,
        boolean chuva,
        MetodoPlantio metodoPlantio,
        String observacoes
) {
    public static PlantioResponseDTO from(RegistroPlantio r) {
        return new PlantioResponseDTO(
                r.getId(),
                r.getNumProtocolo(),
                r.getDataHora(),
                r.getAreaFlorestal().getId(),
                r.getAreaFlorestal().getNome(),
                r.getEspecieVegetal().getId(),
                r.getEspecieVegetal().getNomePopular(),
                r.getColaborador().getId(),
                r.getColaborador().getNomeCompleto(),
                r.getQuantidadeMudas(),
                r.getLatitudeTalhao(),
                r.getLongitudeTalhao(),
                r.getTemperatura(),
                r.getUmidade(),
                r.isChuva(),
                r.getMetodoPlantio(),
                r.getObservacoes()
        );
    }
}
