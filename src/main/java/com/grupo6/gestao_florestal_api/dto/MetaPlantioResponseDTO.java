package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.MetaPlantio;

import java.util.UUID;

public record MetaPlantioResponseDTO(
        UUID id,
        UUID areaFlorestalId,
        String areaFlorestalNome,
        Integer mes,
        Integer ano,
        Integer quantidadeMeta
) {
    public static MetaPlantioResponseDTO from(MetaPlantio m) {
        return new MetaPlantioResponseDTO(
                m.getId(),
                m.getAreaFlorestal().getId(),
                m.getAreaFlorestal().getNome(),
                m.getMes(),
                m.getAno(),
                m.getQuantidadeMeta()
        );
    }
}
