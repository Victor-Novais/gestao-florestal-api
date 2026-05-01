package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.domain.Ocorrencia;
import com.grupo6.gestao_florestal_api.domain.enums.TipoOcorrencia;
import com.grupo6.gestao_florestal_api.domain.enums.UrgenciaOcorrencia;
import com.grupo6.gestao_florestal_api.dto.OcorrenciaConsolidadoItemDTO;
import com.grupo6.gestao_florestal_api.dto.OcorrenciaConsolidadoResponseDTO;
import com.grupo6.gestao_florestal_api.exception.BusinessException;
import com.grupo6.gestao_florestal_api.repository.OcorrenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelatorioOcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;

    @Transactional(readOnly = true)
    public OcorrenciaConsolidadoResponseDTO buscarConsolidado(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);

        LocalDateTime inicioDateTime = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fimDateTime = dataFim != null ? dataFim.atTime(23, 59, 59) : null;

        List<Ocorrencia> ocorrencias = ocorrenciaRepository.buscarProdutividade(inicioDateTime, fimDateTime);
        Map<TipoOcorrencia, ConsolidadoBuilder> agrupado = new EnumMap<>(TipoOcorrencia.class);

        for (Ocorrencia ocorrencia : ocorrencias) {
            ConsolidadoBuilder item = agrupado.computeIfAbsent(
                    ocorrencia.getTipo(),
                    key -> new ConsolidadoBuilder(key)
            );
            item.incrementar(ocorrencia.getUrgencia());
        }

        List<OcorrenciaConsolidadoItemDTO> itens = new ArrayList<>();
        for (TipoOcorrencia tipo : TipoOcorrencia.values()) {
            itens.add(agrupado.getOrDefault(tipo, new ConsolidadoBuilder(tipo)).build());
        }

        return new OcorrenciaConsolidadoResponseDTO(dataInicio, dataFim, itens);
    }

    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new BusinessException("Data inicial não pode ser maior que a data final");
        }
    }

    private static final class ConsolidadoBuilder {
        private final TipoOcorrencia tipo;
        private int baixo;
        private int medio;
        private int alto;
        private int critico;

        private ConsolidadoBuilder(TipoOcorrencia tipo) {
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

        private OcorrenciaConsolidadoItemDTO build() {
            int total = baixo + medio + alto + critico;
            BigDecimal percentualCriticas = total == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(alto + critico)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

            return new OcorrenciaConsolidadoItemDTO(tipo, baixo, medio, alto, critico, total, percentualCriticas);
        }
    }
}
