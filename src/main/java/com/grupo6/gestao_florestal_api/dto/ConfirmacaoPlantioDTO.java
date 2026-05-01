package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.RegistroPlantio;
import com.grupo6.gestao_florestal_api.domain.enums.MetodoPlantio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConfirmacaoPlantioDTO(
        UUID id,
        String numProtocolo,
        LocalDateTime dataHora,
        String areaFlorestalNome,
        String municipio,
        String estado,
        String especieNomePopular,
        String especieNomeCientifico,
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
    public static ConfirmacaoPlantioDTO from(RegistroPlantio r) {
        return new ConfirmacaoPlantioDTO(
                r.getId(),
                r.getNumProtocolo(),
                r.getDataHora(),
                r.getAreaFlorestal().getNome(),
                r.getAreaFlorestal().getMunicipio(),
                r.getAreaFlorestal().getEstado(),
                r.getEspecieVegetal().getNomePopular(),
                r.getEspecieVegetal().getNomeCientifico(),
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
