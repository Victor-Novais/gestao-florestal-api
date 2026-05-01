package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.domain.AreaFlorestal;
import com.grupo6.gestao_florestal_api.domain.InventarioFlorestal;
import com.grupo6.gestao_florestal_api.domain.Ocorrencia;
import com.grupo6.gestao_florestal_api.domain.RegistroPlantio;
import com.grupo6.gestao_florestal_api.domain.enums.Bioma;
import com.grupo6.gestao_florestal_api.domain.enums.EstadoGeral;
import com.grupo6.gestao_florestal_api.domain.enums.StatusArea;
import com.grupo6.gestao_florestal_api.domain.enums.TipoOcorrencia;
import com.grupo6.gestao_florestal_api.domain.enums.UrgenciaOcorrencia;
import com.grupo6.gestao_florestal_api.dto.CoberturaBiomaDTO;
import com.grupo6.gestao_florestal_api.dto.IndiceSaudeFlorestalDTO;
import com.grupo6.gestao_florestal_api.dto.OcorrenciaTipoUrgenciaDTO;
import com.grupo6.gestao_florestal_api.dto.ProdutividadeResponseDTO;
import com.grupo6.gestao_florestal_api.dto.TotalMudasAreaDTO;
import com.grupo6.gestao_florestal_api.exception.BusinessException;
import com.grupo6.gestao_florestal_api.repository.AreaFlorestalRepository;
import com.grupo6.gestao_florestal_api.repository.InventarioFlorestalRepository;
import com.grupo6.gestao_florestal_api.repository.OcorrenciaRepository;
import com.grupo6.gestao_florestal_api.repository.RegistroPlantioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RelatorioProdutividadeService {

    private final RegistroPlantioRepository registroPlantioRepository;
    private final InventarioFlorestalRepository inventarioFlorestalRepository;
    private final OcorrenciaRepository ocorrenciaRepository;
    private final AreaFlorestalRepository areaFlorestalRepository;

    @Transactional(readOnly = true)
    public ProdutividadeResponseDTO buscarIndicadores(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        LocalDateTime inicioDateTime = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fimDateTime = dataFim != null ? dataFim.atTime(23, 59, 59) : null;

        List<RegistroPlantio> plantios = registroPlantioRepository.buscarProdutividade(inicioDateTime, fimDateTime);
        List<InventarioFlorestal> inventarios = inventarioFlorestalRepository.buscarProdutividade(dataInicio, dataFim);
        List<Ocorrencia> ocorrencias = ocorrenciaRepository.buscarProdutividade(inicioDateTime, fimDateTime);
        List<AreaFlorestal> areasAtivas = areaFlorestalRepository.findByAtivoTrueAndStatus(StatusArea.ATIVA);

        return new ProdutividadeResponseDTO(
                dataInicio,
                dataFim,
                montarTotalMudasPorArea(plantios),
                montarCoberturaPorBioma(inventarios, areasAtivas),
                montarOcorrenciasPorTipoUrgencia(ocorrencias),
                montarIndiceSaudeFlorestal(inventarios)
        );
    }

    private List<TotalMudasAreaDTO> montarTotalMudasPorArea(List<RegistroPlantio> plantios) {
        Map<UUID, TotalMudasAreaDTOBuilder> totais = new LinkedHashMap<>();

        for (RegistroPlantio plantio : plantios) {
            TotalMudasAreaDTOBuilder item = totais.computeIfAbsent(
                    plantio.getAreaFlorestal().getId(),
                    id -> new TotalMudasAreaDTOBuilder(id, plantio.getAreaFlorestal().getNome())
            );
            item.totalMudas += plantio.getQuantidadeMudas();
        }

        return totais.values().stream()
                .sorted(Comparator.comparing(TotalMudasAreaDTOBuilder::areaNome))
                .map(item -> new TotalMudasAreaDTO(item.areaId, item.areaNome, item.totalMudas))
                .toList();
    }

    private List<CoberturaBiomaDTO> montarCoberturaPorBioma(List<InventarioFlorestal> inventarios, List<AreaFlorestal> areasAtivas) {
        Map<Bioma, BigDecimal> hectaresTotais = new EnumMap<>(Bioma.class);
        Map<Bioma, BigDecimal> hectaresMonitorados = new EnumMap<>(Bioma.class);
        Map<Bioma, Map<UUID, BigDecimal>> areasMonitoradasPorBioma = new EnumMap<>(Bioma.class);

        for (AreaFlorestal area : areasAtivas) {
            hectaresTotais.merge(area.getBioma(), area.getHectares(), BigDecimal::add);
        }

        for (InventarioFlorestal inventario : inventarios) {
            Bioma bioma = inventario.getAreaFlorestal().getBioma();
            areasMonitoradasPorBioma
                    .computeIfAbsent(bioma, key -> new LinkedHashMap<>())
                    .putIfAbsent(inventario.getAreaFlorestal().getId(), inventario.getAreaFlorestal().getHectares());
        }

        for (Map.Entry<Bioma, Map<UUID, BigDecimal>> entry : areasMonitoradasPorBioma.entrySet()) {
            BigDecimal soma = entry.getValue().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            hectaresMonitorados.put(entry.getKey(), soma);
        }

        List<CoberturaBiomaDTO> cobertura = new ArrayList<>();
        for (Bioma bioma : Bioma.values()) {
            BigDecimal total = hectaresTotais.getOrDefault(bioma, BigDecimal.ZERO);
            BigDecimal monitorado = hectaresMonitorados.getOrDefault(bioma, BigDecimal.ZERO);
            BigDecimal percentual = total.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : monitorado.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);

            cobertura.add(new CoberturaBiomaDTO(bioma, monitorado, total, percentual));
        }

        return cobertura;
    }

    private List<OcorrenciaTipoUrgenciaDTO> montarOcorrenciasPorTipoUrgencia(List<Ocorrencia> ocorrencias) {
        Map<TipoOcorrencia, OcorrenciaTipoUrgenciaBuilder> agrupado = new EnumMap<>(TipoOcorrencia.class);

        for (Ocorrencia ocorrencia : ocorrencias) {
            OcorrenciaTipoUrgenciaBuilder item = agrupado.computeIfAbsent(
                    ocorrencia.getTipo(),
                    key -> new OcorrenciaTipoUrgenciaBuilder(key)
            );
            item.incrementar(ocorrencia.getUrgencia());
        }

        List<OcorrenciaTipoUrgenciaDTO> resultado = new ArrayList<>();
        for (TipoOcorrencia tipo : TipoOcorrencia.values()) {
            OcorrenciaTipoUrgenciaBuilder item = agrupado.getOrDefault(tipo, new OcorrenciaTipoUrgenciaBuilder(tipo));
            resultado.add(item.build());
        }
        return resultado;
    }

    private IndiceSaudeFlorestalDTO montarIndiceSaudeFlorestal(List<InventarioFlorestal> inventarios) {
        if (inventarios.isEmpty()) {
            return new IndiceSaudeFlorestalDTO(BigDecimal.ZERO, 0);
        }

        int soma = inventarios.stream()
                .map(InventarioFlorestal::getEstadoGeral)
                .mapToInt(this::scoreEstadoGeral)
                .sum();

        BigDecimal media = BigDecimal.valueOf(soma)
                .divide(BigDecimal.valueOf(inventarios.size()), 2, RoundingMode.HALF_UP);

        return new IndiceSaudeFlorestalDTO(media, inventarios.size());
    }

    private int scoreEstadoGeral(EstadoGeral estadoGeral) {
        return switch (estadoGeral) {
            case OTIMO -> 4;
            case BOM -> 3;
            case REGULAR -> 2;
            case CRITICO -> 1;
        };
    }

    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new BusinessException("Data inicial não pode ser maior que a data final");
        }
    }

    private static final class TotalMudasAreaDTOBuilder {
        private final UUID areaId;
        private final String areaNome;
        private int totalMudas;

        private TotalMudasAreaDTOBuilder(UUID areaId, String areaNome) {
            this.areaId = areaId;
            this.areaNome = areaNome;
        }

        private String areaNome() {
            return areaNome;
        }
    }

    private static final class OcorrenciaTipoUrgenciaBuilder {
        private final TipoOcorrencia tipo;
        private int baixo;
        private int medio;
        private int alto;
        private int critico;

        private OcorrenciaTipoUrgenciaBuilder(TipoOcorrencia tipo) {
            this.tipo = tipo;
        }

        private void incrementar(UrgenciaOcorrencia urgencia) {
            switch (urgencia) {
                case BAIXO -> baixo++;
                case MEDIO -> medio++;
                case ALTO -> alto++;
                case CRITICO -> critico++;
            }
        }

        private OcorrenciaTipoUrgenciaDTO build() {
            int total = baixo + medio + alto + critico;
            BigDecimal percentualCriticas = total == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(alto + critico)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

            return new OcorrenciaTipoUrgenciaDTO(tipo, baixo, medio, alto, critico, total, percentualCriticas);
        }
    }
}
