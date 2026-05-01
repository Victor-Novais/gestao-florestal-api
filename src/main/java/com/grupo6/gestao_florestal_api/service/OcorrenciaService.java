package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.domain.*;
import com.grupo6.gestao_florestal_api.domain.enums.TipoOcorrencia;
import com.grupo6.gestao_florestal_api.domain.enums.UrgenciaOcorrencia;
import com.grupo6.gestao_florestal_api.dto.OcorrenciaRequestDTO;
import com.grupo6.gestao_florestal_api.dto.OcorrenciaResponseDTO;
import com.grupo6.gestao_florestal_api.event.OcorrenciaAlertaEvent;
import com.grupo6.gestao_florestal_api.exception.EntityNotFoundException;
import com.grupo6.gestao_florestal_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final AreaFlorestalRepository areaRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public OcorrenciaResponseDTO criar(OcorrenciaRequestDTO dto, UUID colaboradorId) {
        AreaFlorestal area = areaRepository.findById(dto.getAreaFlorestalId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Área Florestal não encontrada"));

        Colaborador colaborador = colaboradorRepository.findById(colaboradorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Colaborador não encontrado"));

        Ocorrencia ocorrencia = Ocorrencia.builder()
                .tipo(dto.getTipo())
                .areaFlorestal(area)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .urgencia(dto.getUrgencia())
                .descricao(dto.getDescricao())
                .colaborador(colaborador)
                .build();

        if (dto.getUrlFotos() != null) {
            for (String url : dto.getUrlFotos()) {
                OcorrenciaFoto foto = OcorrenciaFoto.builder()
                        .ocorrencia(ocorrencia)
                        .urlFoto(url)
                        .build();
                ocorrencia.getFotos().add(foto);
            }
        }

        Ocorrencia salvo = ocorrenciaRepository.save(ocorrencia);


        boolean alertaEmitido = dto.getUrgencia() == UrgenciaOcorrencia.ALTO ||
                dto.getUrgencia() == UrgenciaOcorrencia.CRITICO;

        if (alertaEmitido) {
            applicationEventPublisher.publishEvent(new OcorrenciaAlertaEvent(this, salvo));
        }

        return toResponseDTO(salvo, alertaEmitido);
    }

    public OcorrenciaResponseDTO buscarPorId(UUID id) {
        Ocorrencia ocorrencia = ocorrenciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ocorrência não encontrada"));
        return toResponseDTO(ocorrencia);
    }

    public OcorrenciaResponseDTO buscarPorProtocolo(String numeroProtocolo) {
        Ocorrencia ocorrencia = ocorrenciaRepository.findByNumeroProtocolo(numeroProtocolo)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ocorrência não encontrada com protocolo: " + numeroProtocolo));
        return toResponseDTO(ocorrencia);
    }

    public Page<OcorrenciaResponseDTO> listar(
            TipoOcorrencia tipo,
            UrgenciaOcorrencia urgencia,
            UUID areaId,
            UUID colaboradorId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Pageable pageable) {

        Page<Ocorrencia> page;

        if (tipo != null) {
            page = ocorrenciaRepository.findByTipo(tipo, pageable);
        } else if (urgencia != null) {
            page = ocorrenciaRepository.findByUrgencia(urgencia, pageable);
        } else if (areaId != null) {
            page = ocorrenciaRepository.findByAreaFlorestalId(areaId, pageable);
        } else if (colaboradorId != null) {
            page = ocorrenciaRepository.findByColaboradorId(colaboradorId, pageable);
        } else if (dataInicio != null || dataFim != null) {
            LocalDateTime inicio = dataInicio != null ? dataInicio : LocalDateTime.now().minusYears(1);
            LocalDateTime fim = dataFim != null ? dataFim : LocalDateTime.now();
            page = ocorrenciaRepository.findByDataRegistroBetween(inicio, fim, pageable);
        } else {
            page = ocorrenciaRepository.findAll(pageable);
        }

        return page.map(this::toResponseDTO);
    }

    private OcorrenciaResponseDTO toResponseDTO(Ocorrencia ocorrencia) {
        return toResponseDTO(ocorrencia, false);
    }

    private OcorrenciaResponseDTO toResponseDTO(Ocorrencia ocorrencia, boolean alertaEmitido) {
        return OcorrenciaResponseDTO.builder()
                .id(ocorrencia.getId())
                .numeroProtocolo(ocorrencia.getNumeroProtocolo())
                .tipo(ocorrencia.getTipo())
                .areaFlorestalId(ocorrencia.getAreaFlorestal().getId())
                .areaFlorestalNome(ocorrencia.getAreaFlorestal().getNome())
                .latitude(ocorrencia.getLatitude())
                .longitude(ocorrencia.getLongitude())
                .urgencia(ocorrencia.getUrgencia())
                .descricao(ocorrencia.getDescricao())
                .dataRegistro(ocorrencia.getDataRegistro())
                .colaboradorId(ocorrencia.getColaborador().getId())
                .colaboradorNome(ocorrencia.getColaborador().getNomeCompleto())
                .fotos(ocorrencia.getFotos().stream()
                        .map(OcorrenciaFoto::getUrlFoto)
                        .collect(Collectors.toList()))
                .alertaEmitido(alertaEmitido)
                .build();
    }
}